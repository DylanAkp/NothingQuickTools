package dev.dylanakp.nothingquicktools

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader

class HBMControlTile : TileService() {

    override fun onStartListening() {
        super.onStartListening()
        if (isHbmActive()) {
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

        var command = String()

        if (isHbmActive()) {
            command = "su -c 'echo 0 > /sys/panel_feature/hbm_mode'"
            qsTile.state = Tile.STATE_INACTIVE
            qsTile.updateTile()
        } else {
            command = "su -c 'echo 1 > /sys/panel_feature/hbm_mode'"
            qsTile.state = Tile.STATE_ACTIVE
            qsTile.updateTile()
        }
        executeShellCommand(command)
    }


    private fun isHbmActive(): Boolean {
        val command = "su -c 'cat /sys/panel_feature/hbm_mode'"
        val output = executeShellCommand(command)
        return output.trim() == "1"
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
