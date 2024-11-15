package ch.admin.bit.jeap.truststoreplugin.filecertrepo;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class FileCertRepo {

    @Getter
    private final Path repoPath;

    private final List<String> includes;

    private final List<String> extensions;

    public static FileCertRepo of(Path repoPath, List<String> includes, List<String> extensions) {
        return new FileCertRepo(repoPath, includes, extensions);
    }

    public Stream<File> streamCertFiles(final String environment) {
        try {
            IOFileFilter extensionsFilter = new SuffixFileFilter(extensions, IOCase.INSENSITIVE);
            return createCertificateSourcePaths(environment)
                    .map(Path::toFile)
                    .filter(File::exists)
                    .filter(File::isDirectory)
                    .flatMap(certsSourceDir -> getCertFiles(certsSourceDir, extensionsFilter));
        } catch (Exception e) {
            throw FileCertRepoException.fileAccessFailed(e);
        }
    }
    private Stream<File> getCertFiles(File directory, IOFileFilter filter) {
        return FileUtils.listFiles(directory, filter, null).stream();
    }

    private Stream<Path> createCertificateSourcePaths(final String environment) {
        return includes.stream().
                map(repoPath::resolve).
                flatMap(includePath -> this.createIncludeSourcePaths(includePath, environment));
    }

    private Stream<Path> createIncludeSourcePaths(final Path includePath, final String environment) {
        return Stream.of(includePath, includePath.resolve(environment));
    }

}
