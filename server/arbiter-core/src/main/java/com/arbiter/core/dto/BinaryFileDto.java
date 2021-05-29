package com.arbiter.core.dto;

public record BinaryFileDto(byte[] data, String fileName, String extension) {

}
