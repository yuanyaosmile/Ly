package com.leyou.upload.service.impl;

import com.leyou.upload.service.UploadService;
import com.ly.common.exception.GlobalException;
import com.ly.common.vo.CodeMsg;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class UploadserviceImpl implements UploadService {

    public static final List<String> ALOOW_TYPES = Arrays.asList("image/jpeg","image/png","image/bmp");


    @Override
    public String uploadImage(MultipartFile file) {
        //校验文件类型
        String contentType = file.getContentType();
        if (! ALOOW_TYPES.contains(contentType)) {
            throw new GlobalException(CodeMsg.IMAGE_TYPE_ERROR);
        }

        //校验文件内容
        //BufferedImage image = ImageIO.read(file.getInputStream());


        try { //目标路径
            File dest = new File("47.100.238.158/root/img", file.getOriginalFilename());
            //保存文件到本地
            file.transferTo(dest);

        } catch (IOException e) {
            e.printStackTrace();
            log.error("failed", e);
            throw new GlobalException(CodeMsg.UPLOAD_IMAGE_FAILED);
        }

        //返回路径

        return null;
    }
}
