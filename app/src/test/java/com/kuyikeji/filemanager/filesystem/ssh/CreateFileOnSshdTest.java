package com.kuyikeji.filemanager.filesystem.ssh;

import android.os.Environment;

import com.kuyikeji.filemanager.filesystem.ssh.test.BlockFileCreationFileSystemProvider;

import org.apache.sshd.common.file.virtualfs.VirtualFileSystemFactory;
import org.apache.sshd.common.session.Session;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Paths;
import java.util.Collections;


public class CreateFileOnSshdTest extends AbstractSftpServerTest {

    @Test
    public void testCreateFileNormal() throws Exception {
        createSshServer(new VirtualFileSystemFactory(Paths.get(Environment.getExternalStorageDirectory().getAbsolutePath())));
    }

    @Test
    public void testCreateFilePermissionDenied() throws Exception{
        createSshServer(new VirtualFileSystemFactory(){
            @Override
            public FileSystem createFileSystem(Session session) throws IOException {
            return new BlockFileCreationFileSystemProvider().newFileSystem(Paths.get(Environment.getExternalStorageDirectory().getAbsolutePath()), Collections.emptyMap());
            }
        });
    }
}
