package com.example.gemini.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gemini.R
import com.example.gemini.databinding.FragmentGpaBinding
import com.example.gemini.databinding.ItemGpaSubjectBinding
import com.example.gemini.model.GpaSubject
import com.example.gemini.viewmodel.GpaViewModel
import java.util.Locale

class GpaAdapter(
    private val subjects: List<GpaSubject>,
    private val onDeleteClick: (Int) -> Unit
) : RecyclerView.Adapter<GpaAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemGpaSubjectBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemGpaSubjectBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val subject = subjects[position]

        holder.binding.etSubjectName.tag = null
        holder.binding.etSubjectName.setText(subject.name)
        holder.binding.spinnerCredits.setSelection(subject.credits - 1)

        val gradeArray = holder.itemView.context.resources.getStringArray(R.array.grades_array)
        val gradeIndex = gradeArray.indexOf(subject.grade)
        if (gradeIndex >= 0) holder.binding.spinnerGrade.setSelection(gradeIndex)

        holder.binding.etSubjectName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val pos = holder.bindingAdapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    subjects[pos].name = s.toString()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        holder.binding.spinnerCredits.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    pos: Int,
                    id: Long
                ) {
                    val currentPos = holder.bindingAdapterPosition
                    if (currentPos != RecyclerView.NO_POSITION) {
                        subjects[currentPos].credits = pos + 1
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

        holder.binding.spinnerGrade.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    pos: Int,
                    id: Long
                ) {
                    val currentPos = holder.bindingAdapterPosition
                    if (currentPos != RecyclerView.NO_POSITION) {
                        subjects[currentPos].grade = gradeArray[pos]
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

        holder.binding.btnDelete.setOnClickListener {
            val currentPos = holder.bindingAdapterPosition
            if (currentPos != RecyclerView.NO_POSITION) {
                onDeleteClick(currentPos)
            }
        }
    }

    override fun getItemCount() = subjects.size
}

class GpaFragment : Fragment() {
    private var _binding: FragmentGpaBinding? = null
    private val binding get() = _binding!!
    private val viewModel: GpaViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGpaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObservers()

        binding.btnAddSubject.setOnClickListener {
            viewModel.addSubject()
        }

        binding.btnCalculateGpa.setOnClickListener {
            viewModel.calculateGpa()
        }
    }

    private fun setupRecyclerView() {
        val adapter = GpaAdapter(viewModel.subjects.value ?: emptyList()) { position ->
            viewModel.removeSubject(position)
        }
        binding.rvSubjects.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSubjects.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.subjects.observe(viewLifecycleOwner) {
            binding.rvSubjects.adapter?.notifyDataSetChanged()
        }

        viewModel.gpaResult.observe(viewLifecycleOwner) { gpa ->
            if (gpa != null) {
                binding.tvGpaResult.text = String.format(Locale.getDefault(), "평균 평점: %.2f", gpa)
                binding.tvGpaResult.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
