package dev.dylanakp.nothingquicktools

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader

class AutoScreenBrightnessTile : TileService() {

    override fun onStartListening() {
        super.onStartListening()

        if (isGABActive()) {
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
            executeShellCommand("su -c 'settings put system led_auto_brightness_enable 0'")
        } else {
            qsTile.state = Tile.STATE_ACTIVE
            qsTile.updateTile()
            executeShellCommand("su -c 'settings put system screen_brightness_mode 1'")
        }

    }


    private fun isGABActive(): Boolean {
        val command = "su -c 'settings get system screen_brightness_mode'"
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
