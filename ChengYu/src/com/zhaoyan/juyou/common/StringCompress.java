package com.zhaoyan.juyou.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class StringCompress {
	// 压缩
	public static byte[] compress(String str) throws IOException {
		if (str == null || str.length() == 0) {
			return new byte[] {};
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		GZIPOutputStream gzip = new GZIPOutputStream(out);
		gzip.write(str.getBytes("UTF-8"));
		gzip.close();
		return out.toByteArray();
	}

	// 解压缩
	public static String uncompress(byte[] data) throws IOException {
		if (data == null || data.length == 0) {
			return "";
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ByteArrayInputStream in = new ByteArrayInputStream(data);
		GZIPInputStream gunzip = new GZIPInputStream(in);
		byte[] buffer = new byte[256];
		int n;
		while ((n = gunzip.read(buffer)) >= 0) {
			out.write(buffer, 0, n);
		}

		return out.toString("UTF-8");
	}

	public static void main(String[] args) {
		String TEST = "平台默认编码，也可以显式的指定如toString(“GBK”)平台默认编码，也可以显式的指定如toString(GBK)平台默认编码，也可以显式的指定如toString(GBK)平台默认编码，也可以显式的指定如toString(GBK)平台默认编码，也可以显式的指定如toString(GBK)";
		System.out.println("TEST length = " + TEST.length());
		long start = System.currentTimeMillis();
		for (int i = 0; i < 100; i++) {
			long start1 = System.currentTimeMillis();
			try {
				byte[] test = compress(TEST);
				System.out.println(test.length);
				System.out.println(uncompress(test));
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("cost time "
					+ (System.currentTimeMillis() - start1));
		}
		System.out.println("Total time: "
				+ (System.currentTimeMillis() - start));
	}
}
