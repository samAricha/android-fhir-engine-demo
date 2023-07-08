package com.google.android.fhir.codelabs.engine.sdc

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import com.google.android.fhir.codelabs.engine.FhirApplication
import com.google.android.fhir.codelabs.engine.R
import com.google.android.fhir.datacapture.QuestionnaireFragment
import com.google.android.fhir.datacapture.mapping.ResourceMapper
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.Questionnaire

class CarePlanSDCActivity : AppCompatActivity() {

    var questionnaireJsonString: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_care_plan_sdcactivity)

        // add a questionnaire fragment.
        // Configure a QuestionnaireFragment
        questionnaireJsonString = getStringFromAssets("care-plan.json")

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add(
                    R.id.care_plan_fragment_container_view,
                    QuestionnaireFragment.builder().setQuestionnaire(questionnaireJsonString!!).build()
                )
            }
        }

    }

    private fun submitQuestionnaire(){

        val fhirEngine = FhirApplication.fhirEngine(application)
        // Get a questionnaire response
        val fragment = supportFragmentManager.findFragmentById(R.id.care_plan_fragment_container_view)
                as QuestionnaireFragment
        val questionnaireResponse = fragment.getQuestionnaireResponse()

        // Print the response to the log
        val jsonParser = FhirContext.forCached(FhirVersionEnum.R4).newJsonParser()
        val questionnaireResponseString =
            jsonParser.encodeResourceToString(questionnaireResponse)

        Log.d("response", questionnaireResponseString)


        // extract FHIR resources from QuestionnaireResponse.
        lifecycleScope.launch {
            val questionnaire =
                jsonParser.parseResource(questionnaireJsonString) as Questionnaire
            val bundle = ResourceMapper.extract(questionnaire, questionnaireResponse)
//            val fhirCreateResponse = fhirEngine.create(bundle.entry[0].resource)
            val patientResource = bundle.entry[0].resource as Patient
            val fhirCreateResponse = fhirEngine.create(patientResource)

//            val fhirCreateResponse = fhirEngine.create(bundle.entry[0].resource)
            Log.d("extraction result", jsonParser.encodeResourceToString(bundle))
            Log.d("extraction response", fhirCreateResponse.toString())


        }

    }



    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.submit_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.submit) {
            submitQuestionnaire()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getStringFromAssets(fileName: String): String {
        return assets.open(fileName).bufferedReader().use { it.readText() }
    }
}