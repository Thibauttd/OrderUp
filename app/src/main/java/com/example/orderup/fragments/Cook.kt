package com.example.orderup.fragments

import CookAdapter
import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.orderup.R
import com.example.orderup.databinding.CookBinding
import com.example.orderup.model.OrderModel
import com.example.orderup.repository.MenuItemRepository
import com.example.orderup.repository.OrderRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Cook : Fragment() {

    private var _binding: CookBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: CookAdapter
    private lateinit var allOrders: MutableList<OrderModel>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CookBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Création d'un canal de notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "order_ready_channel",
                "Order Ready Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Channel for notifying about orders being ready."
            }

            // Enregistrement du canal avec le NotificationManager
            val notificationManager =
                requireContext().getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }

        allOrders = mutableListOf()

        adapter = CookAdapter(
            onSwipeRight = { order ->
                deleteOrder(order)
            }
        )

        binding.btnLogo.setOnClickListener {
            findNavController().navigate(R.id.action_Cook_to_Menu)
        }

        binding.btnCook.setOnClickListener {
            findNavController().navigate(R.id.action_Cook_to_Role)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val swipedOrder = allOrders[position]

                // Remove the order from the list
                allOrders.removeAt(position)
                adapter.submitList(allOrders)
                adapter.notifyItemRemoved(position)

                // Send notification when the order is ready
                sendNotificationForOrderReady(swipedOrder)
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)

        val orderRepository = OrderRepository()
        orderRepository.getCookableOrders { orders ->
            allOrders.addAll(orders)
            adapter.submitList(allOrders)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Set _binding to null to avoid memory leaks
        _binding = null
    }

    private fun deleteOrder(order: OrderModel) {
        // Remove the order from the list
        allOrders.remove(order)
        adapter.submitList(allOrders)

        // Send notification when the order is deleted
        sendNotificationForOrderReady(order)
    }

    private fun sendNotificationForOrderReady(order: OrderModel) {
        // Check if permission is granted
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.INTERNET
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // If permission is not granted, request it from the user
            // You need to implement the logic to request permission from the user
            // This can be done using ActivityCompat.requestPermissions() or any other appropriate mechanism
            // Then return from the method
            return
        }

        // Build unique ID for the notification
        val notificationId = order.id.hashCode()

        // Create notification content
        val menuItemRepository = MenuItemRepository("")

        // Launch a coroutine
        CoroutineScope(Dispatchers.Main).launch {
            val itemName = menuItemRepository.getItemName(order.menuitemid)

            // Build notification content with the retrieved item name
            val notificationContent = "La commande (${order.quantity} $itemName) est prête à être servie."

            // Build the notification
            val builder = NotificationCompat.Builder(requireContext(), "order_ready_channel")
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("Commande Prête")
                .setContentText(notificationContent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            // Show the notification
            with(NotificationManagerCompat.from(requireContext())) {
                notify(notificationId, builder.build())
            }
        }
    }
}
