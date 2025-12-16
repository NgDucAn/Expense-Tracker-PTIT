package com.ptit.expensetracker.features.ai.ui.chat

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp

/**
 * Minimal markdown renderer for bot messages.
 * Supports:
 * - **bold**
 * - `inline code`
 * - bullet lines starting with "- "
 *
 * This is intentionally small and safe for production (no HTML).
 */
@Composable
fun MarkdownText(
    markdown: String,
    maxLines: Int = Int.MAX_VALUE,
) {
    Text(
        text = markdownToAnnotatedString(markdown),
        style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp),
        maxLines = maxLines,
        overflow = TextOverflow.Clip
    )
}

private fun markdownToAnnotatedString(input: String): AnnotatedString {
    val base = input.replace("\r\n", "\n")
    return buildAnnotatedString {
        var i = 0
        while (i < base.length) {
            // Inline code: `...`
            if (base[i] == '`') {
                val end = base.indexOf('`', startIndex = i + 1)
                if (end != -1) {
                    val code = base.substring(i + 1, end)
                    pushStyle(
                        SpanStyle(
                            fontFamily = FontFamily.Monospace,
                            background = androidx.compose.ui.graphics.Color(0x1F000000)
                        )
                    )
                    append(code)
                    pop()
                    i = end + 1
                    continue
                }
            }

            // Bold: **...**
            if (i + 1 < base.length && base[i] == '*' && base[i + 1] == '*') {
                val end = base.indexOf("**", startIndex = i + 2)
                if (end != -1) {
                    val bold = base.substring(i + 2, end)
                    pushStyle(SpanStyle(fontWeight = FontWeight.SemiBold))
                    append(bold)
                    pop()
                    i = end + 2
                    continue
                }
            }

            append(base[i])
            i++
        }
    }
}


