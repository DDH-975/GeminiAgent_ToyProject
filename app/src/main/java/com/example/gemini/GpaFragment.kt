package com.example.gemini

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gemini.databinding.FragmentGpaBinding
import com.example.gemini.databinding.ItemGpaSubjectBinding
import java.util.Locale

data class GpaSubject(
    var name: String = "",
    var credits: Int = 3,
    var grade: String = "A+",
)

class GpaAdapter(private val subjects: MutableList<GpaSubject>) :
    RecyclerView.Adapter<GpaAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemGpaSubjectBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemGpaSubjectBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val subject = subjects[position]
        
        // Remove listeners before setting values to avoid infinite loops or wrong updates
        holder.binding.etSubjectName.tag = null 
        holder.binding.etSubjectName.setText(subject.name)
        
        holder.binding.spinnerCredits.setSelection(subject.credits - 1)
        
        val gradeArray = holder.itemView.context.resources.getStringArray(R.array.grades_array)
        val gradeIndex = gradeArray.indexOf(subject.grade)
        if (gradeIndex >= 0) holder.binding.spinnerGrade.setSelection(gradeIndex)

        holder.binding.etSubjectName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                subjects[holder.bindingAdapterPosition].name = s.toString()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        holder.binding.spinnerCredits.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                subjects[holder.bindingAdapterPosition].credits = pos + 1
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        holder.binding.spinnerGrade.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                subjects[holder.bindingAdapterPosition].grade = gradeArray[pos]
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        holder.binding.btnDelete.setOnClickListener {
            val currentPos = holder.bindingAdapterPosition
            if (currentPos != RecyclerView.NO_POSITION) {
                subjects.removeAt(currentPos)
                notifyItemRemoved(currentPos)
            }
        }
    }

    override fun getItemCount() = subjects.size
}

class GpaFragment : Fragment() {
    private var _binding: FragmentGpaBinding? = null
    private val binding get() = _binding!!
    private val subjects = mutableListOf(GpaSubject())

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentGpaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val adapter = GpaAdapter(subjects)
        binding.rvSubjects.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSubjects.adapter = adapter

        binding.btnAddSubject.setOnClickListener {
            subjects.add(GpaSubject())
            adapter.notifyItemInserted(subjects.size - 1)
        }

        binding.btnCalculateGpa.setOnClickListener {
            calculateGpa()
        }
    }

    private fun calculateGpa() {
        var totalPoints = 0.0
        var totalCredits = 0.0

        for (subject in subjects) {
            val points = when (subject.grade) {
                "A+" -> 4.5
                "A0" -> 4.0
                "B+" -> 3.5
                "B0" -> 3.0
                "C+" -> 2.5
                "C0" -> 2.0
                "D+" -> 1.5
                "D0" -> 1.0
                "F" -> 0.0
                else -> null // P/NP
            }

            if (points != null) {
                totalPoints += points * subject.credits
                totalCredits += subject.credits.toDouble()
            }
        }

        if (totalCredits > 0) {
            val gpa = totalPoints / totalCredits
            binding.tvGpaResult.text = String.format(Locale.getDefault(), "평균 평점: %.2f", gpa)
            binding.tvGpaResult.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
