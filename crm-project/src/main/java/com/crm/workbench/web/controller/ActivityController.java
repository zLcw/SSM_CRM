package com.crm.workbench.web.controller;

import com.crm.commons.contants.Contants;
import com.crm.commons.domain.ReturnObject;
import com.crm.commons.util.DateUtil;
import com.crm.commons.util.HSSFUtils;
import com.crm.commons.util.UUIDUtils;
import com.crm.settings.domain.User;
import com.crm.settings.service.UserService;
import com.crm.workbench.domain.Activity;
import com.crm.workbench.domain.ActivityRemark;
import com.crm.workbench.service.ActivityRemarkService;
import com.crm.workbench.service.ActivityService;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.*;

@Controller
public class ActivityController {

    @Autowired
    private UserService userService;
    @Autowired
    private ActivityService activityService;
    @Autowired
    private ActivityRemarkService activityRemarkService;

    /**
     * 查询所有的user，并把user的name，id添加到模态窗口的所有者当中
     * @param request
     * @return
     */
    @RequestMapping("/workbench/activity/index.do")
    public String index(HttpServletRequest request){
        // 调用service层方法查询所有用户
        List<User> users = userService.queryAllUsers();
        // 将数据保存到request中
        request.setAttribute("users",users);
        return "workbench/activity/index";
    }

    /**
     * 创建市场活动
     * @param activity
     * @return
     */
    @RequestMapping("/workbench/activity/saveCreateActivity.do")
    public @ResponseBody Object saveCreateActivity(Activity activity, HttpSession session){
        // 进行封装参数
        User user =(User) session.getAttribute(Contants.SESSION_USER);
        activity.setId(UUIDUtils.getUUID());
        activity.setCreateTime(DateUtil.formateDateTime(new Date()));
        activity.setCreateBy(user.getId());

        ReturnObject object = new ReturnObject();
        // 调用service层创建市场活动
        int ret = 0;
        try {
             ret = activityService.saveCreateActivity(activity);
             if (ret > 0){
                 object.setCode(Contants.RETURN_OBJECT_CODE_SUCCESS);
             }else {
                 object.setCode(Contants.RETURN_OBJECT_CODE_FALSE);
                 object.setMessage("系统忙，请稍后重试。。。。");
             }
        }catch (Exception e){
            e.printStackTrace();
        }

        return object;
    }

    /**
     * 分页查询市场活动
     * @param name
     * @param owner
     * @param startDate
     * @param endDate
     * @param pageNo
     * @param pageSize
     * @return
     */
    @RequestMapping("/workbench/activity/queryActivityByConditionForPage.do")
    public @ResponseBody Object queryActivityByConditionForPage(String name,String owner,String startDate,String endDate,
                                                                int pageNo,int pageSize){
        // 封装参数
        Map<String,Object> map = new HashMap<>();
        map.put("name",name);
        map.put("owner",owner);
        map.put("startDate",startDate);
        map.put("endDate",endDate);
        map.put("pageNo",(pageNo-1)*pageSize);
        map.put("pageSize",pageSize);

        // 调用service层查询数据
        List<Activity> activityList = activityService.queryActivityByConditionForPage(map);
        int totalRows = activityService.queryCountOfActivityByCondition(map);

        // 根据查询结果，生成响应信息
        Map<String,Object> retMap = new HashMap<>();
        retMap.put("activityList",activityList);
        retMap.put("totalRows",totalRows);
        return retMap;
    }

    /**
     * 根据ids批量删除市场活动
     * @param id
     * @return
     */
    @RequestMapping("/workbench/activity/deleteActivityByIds.do")
    public @ResponseBody Object deleteActivityByIds(String[] id){
        int code = 0;
        ReturnObject returnObject = new ReturnObject();
        try {
            code = activityService.deleteActivityByIds(id);
            if (code > 0){
                returnObject.setCode(Contants.RETURN_OBJECT_CODE_SUCCESS);
            }else {
                returnObject.setCode(Contants.RETURN_OBJECT_CODE_FALSE);
                returnObject.setMessage("系统忙，请稍后重试.....");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnObject;
    }

    /**
     * 根据id插叙市场活动
     * @param id
     * @return
     */
    @RequestMapping("/workbench/activity/queryActivityById.do")
    public @ResponseBody Object queryActivityById(String id){
        Activity activity = activityService.queryActivityById(id);
        return activity;
    }

    /**
     * 保存修改
     * @param activity
     * @param session
     * @return
     */
    @RequestMapping("/workbench/activity/saveEditActivity.do")
    public @ResponseBody Object saveEditActivity(Activity activity,HttpSession session){
        ReturnObject returnObject = new ReturnObject();
        // 封装参数
        User user = (User)session.getAttribute(Contants.SESSION_USER);
        activity.setEditTime(DateUtil.formateDateTime(new Date()));
        activity.setEditBy(user.getId());
        try {
            int code = activityService.saveEditActivity(activity);
            if (code>0){
                returnObject.setCode(Contants.RETURN_OBJECT_CODE_SUCCESS);
            }else {
                returnObject.setCode(Contants.RETURN_OBJECT_CODE_FALSE);
                returnObject.setMessage("系统忙，请稍后重试...");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnObject;
    }

    /**
     * 批量导出市场活动
     */
    @RequestMapping("/workbench/activity/exportActivitys.do")
    private void exportActivitys(HttpServletResponse response) throws Exception{
        // 调用service层方法，查询所有市场活动
        List<Activity> activityList = activityService.queryAllActivitys();
        // 创建excel文件，并吧activityList写入excel文件中
        HSSFWorkbook wb=new HSSFWorkbook();
        HSSFSheet sheet=wb.createSheet("市场活动列表");
        HSSFRow row=sheet.createRow(0);
        HSSFCell cell=row.createCell(0);
        cell.setCellValue("ID");
        cell=row.createCell(1);
        cell.setCellValue("所有者");
        cell=row.createCell(2);
        cell.setCellValue("名称");
        cell=row.createCell(3);
        cell.setCellValue("开始日期");
        cell=row.createCell(4);
        cell.setCellValue("结束日期");
        cell=row.createCell(5);
        cell.setCellValue("成本");
        cell=row.createCell(6);
        cell.setCellValue("描述");
        cell=row.createCell(7);
        cell.setCellValue("创建时间");
        cell=row.createCell(8);
        cell.setCellValue("创建者");
        cell=row.createCell(9);
        cell.setCellValue("修改时间");
        cell=row.createCell(10);
        cell.setCellValue("修改者");

        //遍历activityList，创建HSSFRow对象，生成所有的数据行
        if(activityList!=null && activityList.size()>0){
            Activity activity=null;
            for(int i=0;i<activityList.size();i++){
                activity=activityList.get(i);

                //每遍历出一个activity，生成一行
                row=sheet.createRow(i+1);
                //每一行创建11列，每一列的数据从activity中获取
                cell=row.createCell(0);
                cell.setCellValue(activity.getId());
                cell=row.createCell(1);
                cell.setCellValue(activity.getOwner());
                cell=row.createCell(2);
                cell.setCellValue(activity.getName());
                cell=row.createCell(3);
                cell.setCellValue(activity.getStartDate());
                cell=row.createCell(4);
                cell.setCellValue(activity.getEndDate());
                cell=row.createCell(5);
                cell.setCellValue(activity.getCost());
                cell=row.createCell(6);
                cell.setCellValue(activity.getDescription());
                cell=row.createCell(7);
                cell.setCellValue(activity.getCreateTime());
                cell=row.createCell(8);
                cell.setCellValue(activity.getCreateBy());
                cell=row.createCell(9);
                cell.setCellValue(activity.getEditTime());
                cell=row.createCell(10);
                cell.setCellValue(activity.getEditBy());
            }
        }
        //根据wb对象生成excel文件
       /* OutputStream os=new FileOutputStream("D:\\course\\18-CRM\\阶段资料\\serverDir\\activityList.xls");
        wb.write(os);*/
        //关闭资源
        /*os.close();
        wb.close();*/

        //把生成的excel文件下载到客户端
        response.setContentType("application/octet-stream;charset=UTF-8");
        response.addHeader("Content-Disposition","attachment;filename=activityList.xls");
        OutputStream out=response.getOutputStream();
        /*InputStream is=new FileInputStream("D:\\course\\18-CRM\\阶段资料\\serverDir\\activityList.xls");
        byte[] buff=new byte[256];
        int len=0;
        while((len=is.read(buff))!=-1){
            out.write(buff,0,len);
        }
        is.close();*/

        wb.write(out);

        wb.close();
        out.flush();

    }

    /**
     * 导入市场活动
     * @param activityFile 市场活动文件
     * @return
     */
    @RequestMapping("/workbench/activity/importActivity.do")
    public @ResponseBody Object importActivity(MultipartFile activityFile,HttpSession session){
        ReturnObject returnObject = new ReturnObject();
        User user = (User) session.getAttribute(Contants.SESSION_USER);
        try {
            // 把excel文件写入磁盘目录文件
            /*String originalFilename = activityFile.getOriginalFilename();
            File file = new File("D:\\desktop\\CRM",originalFilename);
            activityFile.transferTo(file);*/

            // 解析execl 文件，获取文件中的数据，并把数据封装到list集合中
            //根据excel文件生成HSSFWorkbook对象，封装了excel文件的所有信息
            //InputStream is=new FileInputStream("D:\\desktop\\CRM\\"+originalFilename);
            InputStream inputStream = activityFile.getInputStream();
            HSSFWorkbook wb=new HSSFWorkbook(inputStream);
            //根据wb获取HSSFSheet对象，封装了一页的所有信息
            HSSFSheet sheet=wb.getSheetAt(0);//页的下标，下标从0开始，依次增加
            //根据sheet获取HSSFRow对象，封装了一行的所有信息
            HSSFRow row=null;
            HSSFCell cell=null;
            Activity activity = null;
            List<Activity> activityList = new ArrayList<>();
            for(int i=1;i<=sheet.getLastRowNum();i++) {//sheet.getLastRowNum()：最后一行的下标
                row=sheet.getRow(i);//行的下标，下标从0开始，依次增加
                activity = new Activity();
                activity.setId(UUIDUtils.getUUID());
                activity.setOwner(user.getId());
                activity.setCreateTime(DateUtil.formateDateTime(new Date()));
                activity.setCreateBy(user.getId());
                for(int j=0;j<row.getLastCellNum();j++) {//row.getLastCellNum():最后一列的下标+1
                    //根据row获取HSSFCell对象，封装了一列的所有信息
                    cell=row.getCell(j);//列的下标，下标从0开始，依次增加

                    //获取列中的数据
                    String cellValue = HSSFUtils.getCellValueForStr(cell);
                    if (j==0){
                        activity.setName(cellValue);
                    }else if (j==1){
                        activity.setStartDate(cellValue);
                    }else if (j==2){
                        activity.setEndDate(cellValue);
                    }else if (j==3){
                        activity.setCost(cellValue);
                    }else if (j==4){
                        activity.setDescription(cellValue);
                    }
                }

                //每一行中所有的列封装好，保存到list中
                activityList.add(activity);
            }
            // 调用service层方法保存市场活动
            int ret = activityService.saveCreateActivityByList(activityList);
            returnObject.setCode(Contants.RETURN_OBJECT_CODE_SUCCESS);
            returnObject.setRetDate(ret);
        } catch (IOException e) {
            returnObject.setCode(Contants.RETURN_OBJECT_CODE_FALSE);
            returnObject.setMessage("系统忙，请稍后重试...");
            e.printStackTrace();
        }
        return returnObject;
    }

    @RequestMapping("/workbench/activity/detailActivity.do")
    public String detailActivity(String id,HttpServletRequest request){
        // 调用service层查询明细
        Activity activity=activityService.queryActivityForDetailById(id);
        List<ActivityRemark> remarkList=activityRemarkService.queryActivityRemarkForDrtailByActivityById(id);
        //把数据保存到request中
        request.setAttribute("activity",activity);
        request.setAttribute("remarkList",remarkList);
        //请求转发
        return "workbench/activity/detail";

    }

}
