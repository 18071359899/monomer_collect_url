package com.collect.backend.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ByteConverter {
    public static String convertBytesToReadableSize(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            BigDecimal kb = new BigDecimal(bytes).divide(new BigDecimal(1024), 2, RoundingMode.HALF_UP).stripTrailingZeros();
            return kb.toPlainString() + " KB";
        } else if (bytes < 1024 * 1024 * 1024) {
            BigDecimal mb = new BigDecimal(bytes).divide(new BigDecimal(1024 * 1024), 2, RoundingMode.HALF_UP).stripTrailingZeros();
            return mb.toPlainString() + " MB";
        } else if (bytes < 1024L * 1024L * 1024L * 1024L) {
            BigDecimal gb = new BigDecimal(bytes).divide(new BigDecimal(1024L * 1024L * 1024L), 2, RoundingMode.HALF_UP).stripTrailingZeros();
            return gb.toPlainString() + " GB";
        } else {
            BigDecimal tb = new BigDecimal(bytes).divide(new BigDecimal(1024L * 1024L * 1024L * 1024L), 2, RoundingMode.HALF_UP).stripTrailingZeros();
            return tb.toPlainString() + " TB";
        }

    }
}
