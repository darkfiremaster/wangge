package com.shinemo.wangge.web.controller.common;


import com.shinemo.Aace.MutableBoolean;
import com.shinemo.Aace.MutableByteArray;
import com.shinemo.Aace.MutableLong;
import com.shinemo.Aace.RetCode;
import com.shinemo.Aace.context.AaceContext;
import com.shinemo.client.ace.tinyfs.TinyfsService;
import com.shinemo.common.annotation.SmIgnore;
import com.shinemo.common.tools.exception.ApiException;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.common.tools.tfs.FileSystemClient;
import com.shinemo.smartgrid.domain.FileVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Decoder;

import javax.annotation.Resource;
import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/smartGrid/file")
public class SmartGridFilesystemController {
    private static final String FILE_SYSTEM_INNER_NET_URL = "http://fs.shinemo.net/sfs/upload/srvfile";

    @Resource
    private TinyfsService tinyfsService;
    @Resource
    private FileSystemClient fileSystemClient;

    /**
     * 文件下载接口 返回id
     *
     * @param id
     * @return url
     */
    @RequestMapping(value = "/downloadFileForId", method = RequestMethod.GET)
    @SmIgnore
    public ApiResult<byte[]> downloadFileForId(@RequestParam(value = "id") Long id) {
        try {
            log.info("FilesystemController downloadFileForId, id={}", id);
            MutableBoolean finished = new MutableBoolean();
            finished.set(true);
            MutableByteArray data = new MutableByteArray();
            AaceContext aaceContext = new AaceContext();
            // 下载文件
            int ret = tinyfsService.getFile(id, (byte) 2, data, finished,aaceContext);
            if (ret != RetCode.RET_SUCCESS) {
                log.error("FilesystemController downloadFileForId tinyfsClient.getFile error,id={} ret: {}", id, ret);
                throw new ApiException("下载文件失败，请稍后再试", ret);
            }

            return ApiResult.success(data.get());
        } catch (Exception e) {
            log.error("FilesystemController downloadFileForId Exception, e:", e);
            return ApiResult.fail("下载文件失败", -1);
        }
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public ApiResult<Long> upload(MultipartFile file) {
        try {
            // 文件大小检查
            long size = file.getSize();
            if (size > 2 * 1024 * 1024) {
                return ApiResult.fail("文件过大，请上传2M以内文件！", -1);
            }
            MutableLong fileId = new MutableLong();
            AaceContext aaceContext = new AaceContext();
            int ret = tinyfsService.addFile((byte) 2, file.getBytes(), false, true, fileId,aaceContext);
            log.info("FilesystemController upload, ret={}", ret);
            return ApiResult.success(fileId.get());
        } catch (Exception e) {
            log.error("FilesystemController downloadFileForId Exception, e:", e);
            return ApiResult.fail("下载文件失败", -1);
        }
    }

    @RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
    @SmIgnore
    public ApiResult<String> uploadFile(@RequestBody FileVo fileVo) {
        try {
            return transformBase64(fileVo.getContent().split(",")[1]);
        } catch (Exception e) {
            log.error("FilesystemController upload Exception, msg:", e);
            return ApiResult.fail("文件上传错误", -1);
        }
    }

    private ApiResult<String> transformBase64(String str) throws IOException {
        BASE64Decoder decode = new BASE64Decoder();
        byte[] buffer = decode.decodeBuffer(str);
        fileSystemClient.setFileUploadUrl(FILE_SYSTEM_INNER_NET_URL);
        String uploadUrl = fileSystemClient.upload(buffer, 30);
        if (StringUtils.isBlank(uploadUrl)) {
            log.error("FilesystemController getUrl fileSystemClient.upload error, filesystemUrl={}", FILE_SYSTEM_INNER_NET_URL);
            return ApiResult.fail("文件上传错误", -1);
        }
        return ApiResult.success(uploadUrl);
    }
}