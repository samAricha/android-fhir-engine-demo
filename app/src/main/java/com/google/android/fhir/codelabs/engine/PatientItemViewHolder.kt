/*
 * Copyright 2022 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.fhir.codelabs.engine

import android.content.Intent
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.fhir.codelabs.engine.databinding.PatientListItemViewBinding
import com.google.android.fhir.codelabs.engine.sdc.CarePlanSDCActivity
import org.hl7.fhir.r4.model.Patient

class PatientItemViewHolder(binding: PatientListItemViewBinding) :
  RecyclerView.ViewHolder(binding.root), View.OnClickListener {

  init {
    itemView.setOnClickListener(this)
  }

  private val nameTextView: TextView = binding.name
  private val genderTextView: TextView = binding.gender
  private val cityTextView = binding.city

  private lateinit var patientItem: Patient


  fun bind(patientItem: Patient) {
    this.patientItem = patientItem
    nameTextView.text = patientItem.id
    genderTextView.text = patientItem.toString()
    cityTextView.text = patientItem.address.toString()
//    nameTextView.text =
//      patientItem.name.first().let { it.given.joinToString(separator = " ") + " " + it.family }
//    genderTextView.text = patientItem.gender.display
//    cityTextView.text = patientItem.address.singleOrNull()?.city
  }


  override fun onClick(view: View) {
    val context = view.context
    val intent = Intent(context, CarePlanSDCActivity::class.java)
    intent.putExtra("patientId", patientItem.id)
    context.startActivity(intent)
  }

}
