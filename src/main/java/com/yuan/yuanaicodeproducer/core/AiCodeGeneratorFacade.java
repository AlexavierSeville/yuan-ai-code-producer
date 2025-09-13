package com.yuan.yuanaicodeproducer.core;

import cn.hutool.json.JSONUtil;
import com.yuan.yuanaicodeproducer.ai.AiCodeGeneratorService;
import com.yuan.yuanaicodeproducer.ai.AiCodeGeneratorServiceFactory;
import com.yuan.yuanaicodeproducer.ai.model.HtmlCodeResult;
import com.yuan.yuanaicodeproducer.ai.model.MultiFileCodeResult;
import com.yuan.yuanaicodeproducer.ai.model.message.AiResponseMessage;
import com.yuan.yuanaicodeproducer.ai.model.message.ToolExecutedMessage;
import com.yuan.yuanaicodeproducer.ai.model.message.ToolRequestMessage;
import com.yuan.yuanaicodeproducer.constant.AppConstant;
import com.yuan.yuanaicodeproducer.core.builder.VueProjectBuilder;
import com.yuan.yuanaicodeproducer.core.parser.CodeParserExecutor;
import com.yuan.yuanaicodeproducer.core.saver.CodeFileSaverExecutor;
import com.yuan.yuanaicodeproducer.exception.BusinessException;
import com.yuan.yuanaicodeproducer.exception.ErrorCode;
import com.yuan.yuanaicodeproducer.model.enums.CodeGenTypeEnum;
import org.springframework.beans.factory.annotation.Qualifier;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.tool.ToolExecution;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

/**
 * @author Yuan
 * @version 1.0
 * @date 2025-08-22 16:28:28
 * @className AiCodeGeneratorFacade
 * @description 使用外观模式统一管理生成代码和保存代码功能
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiCodeGeneratorFacade {

    private final VueProjectBuilder vueProjectBuilder;
    private final AiCodeGeneratorServiceFactory aiCodeGeneratorServiceFactory;
    
    @Qualifier("codeGenThreadPool")
    private final ExecutorService codeGenThreadPool;

    /**
     * 统一入口：根据类型生成并保存代码
     *
     * @param userMessage     用户提示词
     * @param codeGenTypeEnum 生成类型
     * @param appId           应用 ID
     * @return 保存的目录
     */
    public File generateAndSaveCode(String userMessage, CodeGenTypeEnum codeGenTypeEnum, Long appId) {
        if (codeGenTypeEnum == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "生成类型为空");
        }
        // 通过工厂获取不同的Ai Service服务
        AiCodeGeneratorService aiCodeGeneratorService = aiCodeGeneratorServiceFactory.getAiCodeGeneratorService(appId, codeGenTypeEnum);
        return switch (codeGenTypeEnum) {
            case HTML -> {
                HtmlCodeResult htmlCodeResult = aiCodeGeneratorService.generateHtmlCode(userMessage);
                yield CodeFileSaverExecutor.executeSaver(htmlCodeResult, codeGenTypeEnum, appId);
            }
            case MULTI_FILE -> {
                MultiFileCodeResult multiFileCodeResult = aiCodeGeneratorService.generateMultiFileCode(userMessage);
                yield CodeFileSaverExecutor.executeSaver(multiFileCodeResult, codeGenTypeEnum, appId);
            }
            default -> {
                String errorMessage = "不支持的生成类型：" + codeGenTypeEnum.getValue();
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, errorMessage);
            }
        };
    }

    /**
     * 统一入口：根据类型生成并保存代码(流式)
     *
     * @param userMessage     用户提示词
     * @param codeGenTypeEnum 生成类型
     * @param appId           应用 ID
     * @return 保存的目录
     */
    public Flux<String> generateAndSaveCodeStream(String userMessage, CodeGenTypeEnum codeGenTypeEnum, Long appId) {
        if (codeGenTypeEnum == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "生成类型为空");
        }
        // 通过工厂获取不同的Ai Service服务
        AiCodeGeneratorService aiCodeGeneratorService = aiCodeGeneratorServiceFactory.getAiCodeGeneratorService(appId, codeGenTypeEnum);
        return switch (codeGenTypeEnum) {
            // 在传统的 switch 语句中，case 是不能直接返回一个值的，
            // 你必须在每个 case 中执行一个操作并使用 break 退出。
            // 可是，在增强型 switch 中，你可以直接在每个 case 里执行操作并通过 yield 返回一个值。
            case HTML -> {
                Flux<String> codeStream = aiCodeGeneratorService.generateHtmlCodeStream(userMessage);
                yield processCodeStream(codeStream, codeGenTypeEnum, appId);
            }
            case MULTI_FILE -> {
                Flux<String> codeStream = aiCodeGeneratorService.generateMultiFileCodeStream(userMessage);
                yield processCodeStream(codeStream, codeGenTypeEnum, appId);
            }
            case VUE_PROJECT -> {
                TokenStream tokenStream = aiCodeGeneratorService.generateVueProjectCodeStream(appId, userMessage);
                // 使用适配器模式，将 TokenStream 转换为 Flux<String>
                yield processTokenStream(tokenStream, appId);
            }
            default -> {
                String errorMessage = "不支持的生成类型：" + codeGenTypeEnum.getValue();
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, errorMessage);
            }
        };
    }

    /**
     * 将 TokenStream 转换为 Flux<String>，并传递工具调用信息
     *
     * @param tokenStream TokenStream 对象
     * @return Flux<String> 流式响应
     */
    private Flux<String> processTokenStream(TokenStream tokenStream, Long appId) {
        // 反应式编程写法，通过sink对象往流中写入一些数据
        return Flux.create(sink -> {
            // 监听ai响应内容
            tokenStream
                    // 获取ai响应内容
                    .onPartialResponse((String partialResponse) -> {
                        // 封装到自定义的ai响应类型对象
                        AiResponseMessage aiResponseMessage = new AiResponseMessage(partialResponse);
                        // 把对象解析为json然后写入新的流中
                        sink.next(JSONUtil.toJsonStr(aiResponseMessage));
                    })
                    // 获取工具调用的流式输出
                    .onPartialToolExecutionRequest((index, toolExecutionRequest) -> {
                        ToolRequestMessage toolRequestMessage = new ToolRequestMessage(toolExecutionRequest);
                        sink.next(JSONUtil.toJsonStr(toolRequestMessage));
                    })
                    // 获取工具调用完成后的结果
                    .onToolExecuted((ToolExecution toolExecution) -> {
                        ToolExecutedMessage toolExecutedMessage = new ToolExecutedMessage(toolExecution);
                        sink.next(JSONUtil.toJsonStr(toolExecutedMessage));
                    })
                    // 结束
                    .onCompleteResponse((ChatResponse response) -> {
                        // 执行 Vue 项目构建（同步执行，确保预览时项目已就绪）
                        String projectPath = AppConstant.CODE_OUTPUT_ROOT_DIR + File.separator + "vue_project_" + appId;
                        vueProjectBuilder.buildProject(projectPath);
                        sink.complete();
                    })

                    .onError((Throwable error) -> {
                        error.printStackTrace();
                        sink.error(error);
                    })
                    // 开始监听
                    .start();
        });
    }


    /**
     * *****通用流式代码处理方法*****
     *
     * @param codeStream  代码流
     * @param codeGenType 代码生成类型
     * @param appId       应用 ID
     * @return 流式响应
     */
    private Flux<String> processCodeStream(Flux<String> codeStream, CodeGenTypeEnum codeGenType, Long appId) {
        StringBuilder codeBuilder = new StringBuilder();
        return codeStream
                .doOnNext(chunk -> {
                    // 实时收集代码片段
                    codeBuilder.append(chunk);
                })
                .doOnComplete(() -> {
                    // 流式返回完成后保存代码
                    log.info("开始异步处理代码解析和保存");
                    // 使用专用线程池异步处理代码解析和保存
                    CompletableFuture.runAsync(() -> {
                        try {
                            String completeCode = codeBuilder.toString();
                            log.info("开始解析代码，类型: {}", codeGenType);
                            
                            // 使用执行器解析代码
                            Object parsedResult = CodeParserExecutor.executeParser(completeCode, codeGenType);
                            log.info("代码解析完成，开始保存");
                            
                            // 使用执行器保存代码
                            File savedDir = CodeFileSaverExecutor.executeSaver(parsedResult, codeGenType, appId);
                            log.info("代码保存成功，路径为：{}", savedDir.getAbsolutePath());
                        } catch (Exception e) {
                            log.error("代码处理失败: {}", e.getMessage(), e);
                        }
                    }, codeGenThreadPool)
                    .exceptionally(throwable -> {
                        log.error("异步代码处理异常", throwable);
                        return null;
                    });
                });
    }

    /**
     * 统一入口：生成 HTML 模式的代码并保存代码(流式)
     * 已弃用，使用processCodeStream()替代
     *
     * @param userMessage 用户提示词
     * @return 响应流
     */
    @Deprecated
    private Flux<String> generateAndSaveHtmlCodeStream(String userMessage) {
        // 通过工厂获取不同的Ai Service服务
        AiCodeGeneratorService aiCodeGeneratorService = aiCodeGeneratorServiceFactory.getAiCodeGeneratorService(0);
        Flux<String> result = aiCodeGeneratorService.generateHtmlCodeStream(userMessage);
        // 字符串拼接器，用于当流式返回所有的代码后再保存代码
        StringBuilder codeBuilder = new StringBuilder();
        return result.doOnNext(chunk -> {
            // 实时收集代码片段
            codeBuilder.append(chunk);
        }).doOnComplete(() -> {
            try {
                // 流式完成后保存代码
                String completeHtmlCode = codeBuilder.toString();
                // 解析代码为对象
                HtmlCodeResult htmlCodeResult = CodeParser.parseHtmlCode(completeHtmlCode);
                File saveDir = CodeFileSaver.saveHtmlCodeResult(htmlCodeResult);
                log.info("html文件流式创建完成，保存的目录：{}", saveDir.getAbsolutePath());
            } catch (Exception e) {
                log.error("文件流式创建失败", e);
            }
        });
    }

    /**
     * 统一入口：生成多文件模式代码并保存代码(流式)
     * 已弃用，使用processCodeStream()替代
     *
     * @param userMessage 用户提示词
     * @return 响应流
     */
    @Deprecated
    private Flux<String> generateAndSaveMultiFileCodeStream(String userMessage) {
        // 通过工厂获取不同的Ai Service服务
        AiCodeGeneratorService aiCodeGeneratorService = aiCodeGeneratorServiceFactory.getAiCodeGeneratorService(0);
        Flux<String> result = aiCodeGeneratorService.generateMultiFileCodeStream(userMessage);
        // 字符串拼接器，用于当流式返回所有的代码后再保存代码
        StringBuilder codeBuilder = new StringBuilder();
        return result.doOnNext(chunk -> {
            // 实时收集代码片段
            codeBuilder.append(chunk);
        }).doOnComplete(() -> {
            try {
                // 流式完成后保存代码
                String completeMultiFileCode = codeBuilder.toString();
                // 解析代码为对象
                MultiFileCodeResult multiFileCodeResult = CodeParser.parseMultiFileCode(completeMultiFileCode);
                File saveDir = CodeFileSaver.saveMultiFileCodeResult(multiFileCodeResult);
                log.info("多文件流式创建完成，保存的目录：{}", saveDir.getAbsolutePath());
            } catch (Exception e) {
                log.error("文件流式创建失败", e);
            }
        });
    }

    /**
     * 生成 HTML 模式的代码并保存
     * 已弃用，新方法在CodeFileSaverExecutor中定义
     *
     * @param userMessage 用户提示词
     * @return 保存的目录
     */
    @Deprecated
    private File generateAndSaveHtmlCode(String userMessage) {
        // 通过工厂获取不同的Ai Service服务
        AiCodeGeneratorService aiCodeGeneratorService = aiCodeGeneratorServiceFactory.getAiCodeGeneratorService(0);
        HtmlCodeResult result = aiCodeGeneratorService.generateHtmlCode(userMessage);
        return CodeFileSaver.saveHtmlCodeResult(result);
    }

    /**
     * 生成多文件模式的代码并保存
     * 已弃用，新方法在CodeFileSaverExecutor中定义
     *
     * @param userMessage 用户提示词
     * @return 保存的目录
     */
    @Deprecated
    private File generateAndSaveMultiFileCode(String userMessage) {
        // 通过工厂获取不同的Ai Service服务
        AiCodeGeneratorService aiCodeGeneratorService = aiCodeGeneratorServiceFactory.getAiCodeGeneratorService(0);
        MultiFileCodeResult result = aiCodeGeneratorService.generateMultiFileCode(userMessage);
        return CodeFileSaver.saveMultiFileCodeResult(result);
    }
}

