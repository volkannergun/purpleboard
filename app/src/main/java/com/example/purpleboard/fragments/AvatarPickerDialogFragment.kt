package com.example.purpleboard.fragments // Adjust package name

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.purpleboard.adapters.AvatarAdapter
import com.example.purpleboard.databinding.DialogAvatarPickerBinding // Generated ViewBinding

class AvatarPickerDialogFragment : DialogFragment() {

    private var _binding: DialogAvatarPickerBinding? = null
    private val binding get() = _binding!!

    private var onAvatarSelectedListener: ((String) -> Unit)? = null

    fun setOnAvatarSelectedListener(listener: (String) -> Unit) {
        this.onAvatarSelectedListener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogAvatarPickerBinding.inflate(layoutInflater)

        val avatarNames = mutableListOf<String>()
        for (i in 1..20) {
            avatarNames.add("avatar$i") // Assuming avatar files are named avatar1.png, avatar2.png, etc.
            // in res/drawable/avatars/ (or just res/drawable/)
        }

        val avatarAdapter = AvatarAdapter(requireContext(), avatarNames) { selectedAvatarName ->
            onAvatarSelectedListener?.invoke(selectedAvatarName)
            dismiss()
        }

        binding.recyclerViewAvatars.apply {
            layoutManager = GridLayoutManager(context, 4) // 4 columns
            adapter = avatarAdapter
        }

        return AlertDialog.Builder(requireActivity())
            .setView(binding.root)
            // No buttons needed in the dialog itself if item click dismisses
            .create()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Avoid memory leaks
    }

    companion object {
        const val TAG = "AvatarPickerDialog"
        fun newInstance(): AvatarPickerDialogFragment {
            return AvatarPickerDialogFragment()
        }
    }
}