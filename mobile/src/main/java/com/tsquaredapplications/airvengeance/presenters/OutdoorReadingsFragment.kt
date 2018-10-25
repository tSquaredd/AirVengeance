package com.tsquaredapplications.airvengeance.presenters



import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.tsquaredapplications.airvengeance.R
import com.tsquaredapplications.airvengeance.R.id.*
import com.tsquaredapplications.airvengeance.data.ReadingsViewModel
import kotlinx.android.synthetic.main.fragment_readings.*
import java.math.RoundingMode
import java.net.URL
import java.util.ArrayList
import java.text.DecimalFormat


class OutdoorReadingsFragment : Fragment() {

    private val viewModel by lazy { ViewModelProviders.of(this).get(ReadingsViewModel::class.java) }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_readings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup temp gauge for preferred metric
        when (viewModel.isMetric()) {
            true -> {
                fahrenheit_temperature_gauge.visibility = View.INVISIBLE
                celsius_temperature_gauge.visibility = View.VISIBLE
            }
            false -> {
                celsius_temperature_gauge.visibility = View.INVISIBLE
                fahrenheit_temperature_gauge.visibility = View.VISIBLE
            }
        }

        val task = weatherTask()
        task.execute()
    }


    fun updateUI(data : ArrayList<String>){
        when(viewModel.isMetric()){
            true -> {
                val cTemp = (data[0].toDouble() - 32) * 5 / 9
                celsius_temperature_gauge.setSpeed(cTemp.toFloat())
            }
            false -> {
                fahrenheit_temperature_gauge.setSpeed(data[0].toFloat())
            }
        }
        humidity_gauge.setSpeed(data[2].toFloat())
        val pressure = (data[1].toFloat() /  33.863886666667).toFloat()
        pressure_gauge.setSpeed(pressure)
    }

    inner class weatherTask:AsyncTask<Int,Int,ArrayList<String>>(){
        override fun doInBackground(vararg params: Int?): ArrayList<String> {
            val tph = ArrayList<String>()
            val apiKey = "f8c7615401fe9cbca15ce07b8241bd39"
            val testZipCode = "14224,us"
            val urlString = "https://api.openweathermap.org/data/2.5/weather?q=$testZipCode&units=imperial&appid=$apiKey"
            val result = URL(urlString).readText()
            val resultToString = result
            tph.add(0, resultToString.split("temp\":".toRegex(), 2).toTypedArray()[1].split(",".toRegex(), 2).toTypedArray()[0])
            tph.add(1, resultToString.split("pressure\":".toRegex(), 2).toTypedArray()[1].split(",".toRegex(), 2).toTypedArray()[0])
            tph.add(2, resultToString.split("humidity\":".toRegex(), 2).toTypedArray()[1].split(",".toRegex(), 2).toTypedArray()[0])

            System.out.println(tph[0]+" press"+ tph[1]+"humid "+tph[2])
            return tph
        }

        override fun onPostExecute(result: ArrayList<String>?) {
            super.onPostExecute(result)
            result?.let {
                updateUI(it)
            }
        }

    }
}

