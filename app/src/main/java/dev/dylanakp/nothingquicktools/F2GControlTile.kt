package dev.dylanakp.nothingquicktools

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader

class F2GControlTile : TileService() {

    override fun onStartListening() {
        super.onStartListening()

        if (isF2GActive()) {
            qsTile.state = Tile.STATE_ACTIVE
            qsTile.updateTile()
        } else {
            qsTile.state = Tile.STATE_INACTIVE
            qsTile.updateTile()
        }
    }

    override fun onStopListening() {
        super.onStopListening()
    }

    override fun onClick() {
        super.onClick()

        if (qsTile.state.equals(Tile.STATE_ACTIVE)) {
            qsTile.state = Tile.STATE_INACTIVE
            qsTile.updateTile()
            executeShellCommand("su -c 'settings put global led_effect_gestures_flip_ebable 0'")
        } else {
            qsTile.state = Tile.STATE_ACTIVE
            qsTile.updateTile()
            executeShellCommand("su -c 'settings put global led_effect_gestures_flip_ebable 1'")
        }

    }


    private fun isF2GActive(): Boolean {
        val command = "su -c 'settings get global led_effect_gestures_flip_ebable'"
        val output = executeShellCommand(command)
        return output == "1"
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
