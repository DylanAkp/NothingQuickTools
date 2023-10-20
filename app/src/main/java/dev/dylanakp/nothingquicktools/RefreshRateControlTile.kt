package dev.dylanakp.nothingquicktools

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader

class RefreshRateControlTile : TileService() {

    override fun onStartListening() {
        super.onStartListening()

        qsTile.state = Tile.STATE_ACTIVE

        var state = RefreshRateState();
        if (state == "0") {
            qsTile.subtitle = getString(R.string.dynamic_rate)
        } else if (state == "1") {
            qsTile.subtitle = getString(R.string.high_rate)
        } else {
            qsTile.subtitle = getString(R.string.standard_rate)
        }
        qsTile.updateTile()
    }

    override fun onStopListening() {
        super.onStopListening()
    }

    override fun onClick() {
        super.onClick()

        if (qsTile.subtitle == getString(R.string.dynamic_rate)) {
            qsTile.subtitle = getString(R.string.high_rate)
            executeShellCommand("su -c 'settings put system display_refresh_rate_mode 1'")
            qsTile.updateTile()
        } else if (qsTile.subtitle == getString(R.string.high_rate)){
            qsTile.subtitle = getString(R.string.standard_rate)
            executeShellCommand("su -c 'settings put system display_refresh_rate_mode 2'")
            qsTile.updateTile()
        } else {
            qsTile.subtitle = getString(R.string.dynamic_rate)
            executeShellCommand("su -c 'settings put system display_refresh_rate_mode 0'")
            qsTile.updateTile()
        }

    }


    private fun RefreshRateState(): String {
        val command = "su -c 'settings get system display_refresh_rate_mode'"
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
