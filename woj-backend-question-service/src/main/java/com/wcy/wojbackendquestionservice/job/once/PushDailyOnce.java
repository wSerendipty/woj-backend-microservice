package com.wcy.wojbackendquestionservice.job.once;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wcy.wojbackendcommon.common.ErrorCode;
import com.wcy.wojbackendcommon.exception.BusinessException;
import com.wcy.wojbackendmodel.model.entity.Daily;
import com.wcy.wojbackendmodel.model.entity.Question;
import com.wcy.wojbackendquestionservice.service.DailyService;
import com.wcy.wojbackendquestionservice.service.QuestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * 启动执行一次推送每日一题
 *
 */
// todo 取消注释开启任务
@Component
@Slf4j
public class PushDailyOnce implements CommandLineRunner {

    @Resource
    private QuestionService questionService;
    @Resource
    private DailyService dailyService;

    @Override
    public void run(String... args) {
        // 获取今天的开始和结束时间（包括整个24小时）
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay(); // 今天0点
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay(); // 明天0点，实际上代表今天23:59:59
        // 转换为Java.util.Date类型，取决于数据库字段的实际类型
        Date startDate = Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(endOfDay.atZone(ZoneId.systemDefault()).minusNanos(1).toInstant()); // 减去一纳秒以确保不包含第二天
        QueryWrapper<Daily> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("belongType", "question");
        queryWrapper.between("createTime", startDate, endDate);
        Daily dailyServiceOne = dailyService.getOne(queryWrapper);
        if (dailyServiceOne != null) {
            log.info("今日已经推送过每日一题");
            return;
        }
        QueryWrapper<Question> questionQueryWrapper = new QueryWrapper<>();
        questionQueryWrapper.orderByAsc("RAND()");
        questionQueryWrapper.last("LIMIT 1");
        Question question = questionService.getOne(questionQueryWrapper);
        if (question == null){
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        Daily daily = new Daily();
        daily.setQuestionId(question.getId());
        daily.setBelongType("question");
        boolean save = dailyService.save(daily);
        if (save) {
            log.info("启动任务 end, 推送每日一题成功");
        }else {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
    }

}
