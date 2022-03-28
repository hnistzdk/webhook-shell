package com.zdk.webhookshell.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;

/**
 * @author zdk
 * @date 2022/3/28 13:48
 */
@RestController
public class WebHookShellController {

    private static final Logger logger = LoggerFactory.getLogger(WebHookShellController.class);

    /**
     * 请求
     * @param userAgent
     * @param giteeToken
     * @param giteeEvent
     * @return
     * @throws IOException
     */
    @PostMapping("/webhook")
    public String execute(@RequestHeader("User-Agent") String userAgent,
                       @RequestHeader("X-Gitee-Token") String giteeToken,
                       @RequestHeader("X-Gitee-Event") String giteeEvent) throws IOException {
        logger.info("User-Agent:{}", userAgent);
        logger.info("X-Gitee-Token:{}", giteeToken);
        logger.info("Gitee-Event:{}", giteeEvent);
        String token = "zdk";
        if("git-oschina-hook".equals(userAgent)
                && "Push Hook".equals(giteeEvent)
                && token.equals(giteeToken)){
            executeShell();
            return "ok";
        }
        return "非法调用";
    }


    /**
     * 执行脚本
     * @throws IOException
     */
    public void executeShell() throws IOException {
        String startFileName = "notes-start.sh";
        String fullName = getFullName(startFileName);
        File file = new File(fullName);
        if(!file.exists()) {
            logger.info("error:file {} not existed!", fullName);
            return;
        }
        ProcessBuilder processBuilder = new ProcessBuilder("/bin/chmod", "755", fullName);
        processBuilder.directory(new File("/root/my-notes"));
        processBuilder.command("./notes-start.sh");
        Process process = processBuilder.start();

        int runningStatus = 0;
        try {
            runningStatus = process.waitFor();
        } catch (InterruptedException e) {
            logger.info("error:shell", e);
        }

        if(runningStatus != 0) {
            logger.info("error:failed.");
        }else {
            logger.info("success.");
        }
    }

    /**
     * 文件调用全路径
     * @param fileName
     * @return
     */
    private String getFullName(String fileName) {
        String directory = "/root/my-notes";
        return directory + File.separator + fileName;
    }
}
