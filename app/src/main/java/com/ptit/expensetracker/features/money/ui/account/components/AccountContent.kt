package com.ptit.expensetracker.features.money.ui.account.components

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.ptit.expensetracker.BuildConfig
import com.ptit.expensetracker.R
import com.ptit.expensetracker.features.money.ui.account.AccountIntent
import com.ptit.expensetracker.features.money.ui.account.AccountState
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import com.ptit.expensetracker.ui.theme.AppColor
import com.ptit.expensetracker.ui.theme.AppColor.Light.PrimaryColor.cardColor
import com.ptit.expensetracker.ui.theme.AppColor.Light.ReportButtonBackground
import com.ptit.expensetracker.ui.theme.TextMain
import com.ptit.expensetracker.ui.theme.TextSecondary

@Composable
fun AccountContent(
    state: AccountState,
    onIntent: (AccountIntent) -> Unit,
    onNavigateToWallet: () -> Unit,
    onNavigateToCategories: () -> Unit,
    onNavigateToDebts: () -> Unit,
    version: String = BuildConfig.APP_VERSION_NAME,
    modifier: Modifier = Modifier
) {
    val paddingValues = WindowInsets.statusBars.asPaddingValues()
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppColor.Light.PrimaryColor.containerColor)
            .padding(horizontal = 16.dp)
            .padding(top = paddingValues.calculateTopPadding()),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.account_title),
                color = TextMain,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            ProfileCard(state, onIntent)
            FeatureMenu(onNavigateToWallet, onNavigateToCategories, onNavigateToDebts)
            AccountActionsCard(state, onIntent)
        }
        VersionInfo(version)
    }
}

@Composable
private fun ProfileCard(
    state: AccountState,
    onIntent: (AccountIntent) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = cardColor
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!state.isSignedIn) {
                Button(
                    onClick = { onIntent(AccountIntent.SignIn) },
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = ReportButtonBackground,
                        containerColor = Color.White
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.logo_google),
                            contentDescription = stringResource(R.string.account_sign_in_cd),
                            modifier = Modifier.size(40.dp),
                            tint = Color.Unspecified
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (state.isSigningIn) stringResource(R.string.account_signing_in) else stringResource(R.string.account_sign_in_with_google),
                            color = TextMain,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            } else {
                AsyncImage(
                    model = state.photoUrl,
                    contentDescription = stringResource(R.string.account_profile_photo_cd),
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = state.displayName ?: stringResource(R.string.account_user_default),
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Composable
private fun AccountActionsCard(
    state: AccountState,
    onIntent: (AccountIntent) -> Unit
) {
    val context = LocalContext.current as Context
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = cardColor
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { onIntent(AccountIntent.BackupData(context)) },
                enabled = !state.isBackupLoading,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.textButtonColors(
                    contentColor =  AppColor.Light.ReportButtonBackground,
                    containerColor = Color.White,
                )
            ) {
                if (state.isBackupLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(text = stringResource(R.string.account_backup_data), color = TextMain)
            }
            Button(
                onClick = { onIntent(AccountIntent.RestoreData(context)) },
                enabled = !state.isRestoreLoading,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.textButtonColors(
                    contentColor =  AppColor.Light.ReportButtonBackground,
                    containerColor = Color.White,
                )
            ) {
                if (state.isRestoreLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(text = stringResource(R.string.account_restore_data), color = TextMain)
            }
            Button(
                onClick = { onIntent(AccountIntent.SignOut) },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.textButtonColors(
                    contentColor =  AppColor.Light.ReportButtonBackground,
                    containerColor = Color.White,
                )
            ) {
                Text(text = stringResource(R.string.account_sign_out), color = Color(0xFFFF383C))
            }
        }
    }
}

@Composable
private fun FeatureMenu(
    onWallet: () -> Unit,
    onCategories: () -> Unit,
    onDebts: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = cardColor
    ) {
        Column {
            FeatureItem(
                iconRes = R.drawable.ic_basic_wallet,
                title = stringResource(R.string.account_wallets_section),
                onClick = onWallet
            )
            HorizontalDivider(color = Color.Gray.copy(alpha = 0.3f))
            FeatureItem(
                iconRes = R.drawable.ic_category_placeholder,
                title = stringResource(R.string.account_category_section),
                onClick = onCategories
            )
            HorizontalDivider(color = Color.Gray.copy(alpha = 0.3f))
            FeatureItem(
                iconRes = R.drawable.ic_category_credit,
                title = stringResource(R.string.account_debts_section),
                onClick = onDebts
            )
        }
    }
}

@Composable
private fun FeatureItem(
    iconRes: Int,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = title,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = title,
            color = TextMain,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        Icon(
            painter = painterResource(id = R.drawable.ic_arrow_down_24dp),
            contentDescription = stringResource(R.string.account_navigate_cd),
            tint = TextSecondary,
            modifier = Modifier.rotate(-90f)
        )
    }
}

@Composable
private fun VersionInfo(version: String) {
    Text(
        text = stringResource(R.string.account_version, version),
        style = MaterialTheme.typography.bodySmall,
        color = Color.Gray,
        modifier = Modifier
            .padding(bottom = 8.dp)
            .fillMaxWidth(),
        textAlign = androidx.compose.ui.text.style.TextAlign.Center
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun AccountContentPreview() {
    MaterialTheme(colorScheme = darkColorScheme()) {
        AccountContent(
            state = AccountState(
                isSignedIn = false,
                displayName = "Nguyen Van A",
                photoUrl = null,
                isSigningIn = false,
                isBackupLoading = false,
                isRestoreLoading = false,
                error = null
            ),
            onIntent = {},
            onNavigateToWallet = {},
            onNavigateToCategories = {},
            onNavigateToDebts = {},
            version = BuildConfig.APP_VERSION_NAME
        )
    }
} 