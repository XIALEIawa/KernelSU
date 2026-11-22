package me.weishu.kernelsu.ui.screen.shizuku

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import me.weishu.kernelsu.R

@Composable
internal fun ShizukuMaterial(
    state: ShizukuState,
    bottomInnerPadding: Dp,
    onRequestPermission: () -> Unit,
    onOpenApp: () -> Unit,
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.shizuku)) }) },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            contentPadding = PaddingValues(
                top = innerPadding.calculateTopPadding() + 12.dp,
                bottom = innerPadding.calculateBottomPadding() + bottomInnerPadding,
            ),
        ) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = stringResource(R.string.shizuku),
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Text(
                            text = stringResource(R.string.shizuku_summary),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    HorizontalDivider()
                    StatusRow(
                        stringResource(R.string.shizuku_label_installed),
                        stringResource(if (state.installed) R.string.shizuku_installed else R.string.shizuku_not_installed),
                        state.installed,
                    )
                    StatusRow(
                        stringResource(R.string.shizuku_label_running),
                        stringResource(if (state.running) R.string.shizuku_running else R.string.shizuku_not_running),
                        state.running,
                    )
                    StatusRow(
                        stringResource(R.string.shizuku_label_permission),
                        stringResource(if (state.granted) R.string.shizuku_granted else R.string.shizuku_denied),
                        state.granted,
                    )
                }
            }
            item {
                Card(modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) {
                    ActionRow(stringResource(R.string.shizuku_request_permission), onRequestPermission)
                    HorizontalDivider()
                    ActionRow(stringResource(R.string.shizuku_open_app), onOpenApp)
                }
            }
        }
    }
}

@Composable
private fun StatusRow(label: String, value: String, positive: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = if (positive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun ActionRow(label: String, onClick: () -> Unit) {
    Text(
        text = label,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        style = MaterialTheme.typography.bodyLarge,
    )
}
