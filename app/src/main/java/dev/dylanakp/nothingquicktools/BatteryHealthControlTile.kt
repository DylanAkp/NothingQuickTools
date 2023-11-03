package dev.dylanakp.nothingquicktools

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader

class BatteryHealthControlTile : TileService() {

    override fun onStartListening() {
        super.onStartListening()

        refreshBatteryHealth()
    }

    private fun refreshBatteryHealth() {
        val advertisedBattery = "4700000"
        var batteryCapacity = getBatteryCapacity()

        var batteryHealth = (advertisedBattery.toInt() / batteryCapacity.toInt()).toString() + "%"

        qsTile.subtitle = batteryHealth
    }

    override fun onStopListening() {
        super.onStopListening()
    }

    override fun onClick() {
        super.onClick()

        refreshBatteryHealth()
        qsTile.subtitle = "Hello"
    }


    private fun getBatteryCapacity(): String {
        val command = "su -c cat /sys/class/power_supply/battery/charge_full"
        val output = executeShellCommand(command)
        return output.trim()
    }

    private fun executeShellCommand(command: String): String {
        val process = Runtime.getRuntime().exec("sh")
        val outputStream = DataOutputStream(process.outputStream)
        val inputStream = process.inputStream
        outputStream.writeBytes("$command\n")
        outputStream.flush()
        outputStream.writeBytes("exit\n")
        outputStream.flush()
        process.waitFor()
        val reader = BufferedReader(InputStreamReader(inputStream))
        val output = StringBuilder()
        var line: String? = reader.readLine()
        while (line != null) {
            output.append(line)
            line = reader.readLine()
        }
        return output.toString()
    }
}
