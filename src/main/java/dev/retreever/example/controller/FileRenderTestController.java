package dev.retreever.example.controller;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping("/api/v1/public/test/files")
public class FileRenderTestController {

    private static final Path FILE_DIRECTORY = Path.of("files");

    @GetMapping(value = "/csv", produces = "text/csv")
    public ResponseEntity<byte[]> issueCsv() {
        return fileResponse("example.csv", "text/csv");
    }

    @GetMapping(value = "/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> issuePdf() {
        return fileResponse("example.pdf", MediaType.APPLICATION_PDF_VALUE);
    }

    @GetMapping(value = "/mp3", produces = "audio/mpeg")
    public ResponseEntity<byte[]> issueMp3() {
        return fileResponse("example.mp3", "audio/mpeg");
    }

    @GetMapping(value = "/mp4", produces = "video/mp4")
    public ResponseEntity<byte[]> issueMp4() {
        return fileResponse("example.mp4", "video/mp4");
    }

    @GetMapping(value = "/doc", produces = "application/msword")
    public ResponseEntity<byte[]> issueDoc() {
        return fileResponse("file-sample_100kB.doc", "application/msword");
    }

    @GetMapping(value = "/docx", produces = "application/vnd.openxmlformats-officedocument.wordprocessingml.document")
    public ResponseEntity<byte[]> issueDocx() {
        return fileResponse("file-sample_100kB.docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
    }

    @GetMapping(value = "/ppt", produces = "application/vnd.ms-powerpoint")
    public ResponseEntity<byte[]> issuePpt() {
        return fileResponse("file_example_PPT_500kB.ppt", "application/vnd.ms-powerpoint");
    }

    @GetMapping(value = "/xls", produces = "application/vnd.ms-excel")
    public ResponseEntity<byte[]> issueXls() {
        return fileResponse("file_example_XLS_10.xls", "application/vnd.ms-excel");
    }

    @GetMapping(value = "/xlsx", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<byte[]> issueXlsx() {
        return fileResponse("file_example_XLSX_10.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    }

    @GetMapping(value = "/zip", produces = "application/zip")
    public ResponseEntity<byte[]> issueZip() {
        return fileResponse("imagezip.zip", "application/zip");
    }

    private ResponseEntity<byte[]> fileResponse(String fileName, String contentType) {
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.inline().filename(fileName).build().toString())
                .header(HttpHeaders.CACHE_CONTROL, "no-store, no-cache, must-revalidate, max-age=0")
                .body(readFileBytes(fileName));
    }

    private byte[] readFileBytes(String fileName) {
        try {
            return Files.readAllBytes(FILE_DIRECTORY.resolve(fileName));
        } catch (IOException ex) {
            throw new UncheckedIOException("Failed to read test file: " + fileName, ex);
        }
    }
}
