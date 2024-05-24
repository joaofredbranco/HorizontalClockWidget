package com.example.horizontalclockwidget

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.horizontalclockwidget.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d("MainActivity", "onCreate: Iniciando MainActivity")

        // Log para verificar visibilidade do botão
        Log.d("MainActivity", "Botão 'Configure' visível: ${binding.configureButton.visibility == View.VISIBLE}")

        // Inicializa a visualização do relógio horizontal
        updateClockView()

        binding.configureButton.setOnClickListener {
            Log.d("MainActivity", "Botão 'Configure' clicado")
            val intent = Intent(this, ConfigActivity::class.java)
            startActivity(intent)
            Log.d("MainActivity", "ConfigActivity iniciado")
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("MainActivity", "onResume: Atualizando a visualização do relógio")
        // Atualiza o tempo quando a atividade é retomada
        updateClockView()
    }

    private fun updateClockView() {
        Log.d("MainActivity", "updateClockView: Iniciando atualização da visualização do relógio")
        val startTime = PreferencesManager.getStartTime(this)
        val endTime = PreferencesManager.getEndTime(this)
        val interval = PreferencesManager.getInterval(this)
        val barColor = PreferencesManager.getBarColor(this)
        Log.d("MainActivity", "updateClockView: Configurações - startTime: $startTime, endTime: $endTime, interval: $interval, barColor: $barColor")
        binding.horizontalClockView.setTimeConfig(startTime, endTime, interval, barColor)
    }
}
