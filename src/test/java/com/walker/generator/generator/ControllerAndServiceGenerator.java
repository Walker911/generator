package com.walker.generator.generator;

import com.google.common.base.CaseFormat;
import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.walker.generator.common.ProjectConstant.*;

/**
 * 生成 Controller and Service
 */
public class ControllerAndServiceGenerator {

    private static final String PROJECT_PATH = System.getProperty("user.dir");//项目在硬盘上的基础路径

    private static final String TEMPLATE_FILE_PATH = PROJECT_PATH + "/src/test/resources/template";//模板位置

    private static final String AUTHOR = "CodeGenerator";//@author

    private static final String DATE = new SimpleDateFormat("yyyy-MM-dd").format(new Date());//@date

    private static final String JAVA_PATH = "/src/main/java"; //java文件路径

    private static final String PACKAGE_PATH_SERVICE = packageConvertPath(SERVICE_PACKAGE);//生成的Service存放路径

    private static final String PACKAGE_PATH_SERVICE_IMPL = packageConvertPath(SERVICE_IMPL_PACKAGE);//生成的Service实现存放路径

    private static final String PACKAGE_PATH_CONTROLLER = packageConvertPath(CONTROLLER_PACKAGE);//生成的Controller存放路径

    private static final Logger logger = LoggerFactory.getLogger(ControllerAndServiceGenerator.class);

    /**
     * freemarker 配置
     * @return
     * @throws IOException
     */
    private static Configuration getConfiguration() throws IOException {
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_28);
        configuration.setDirectoryForTemplateLoading(new File(TEMPLATE_FILE_PATH));
        configuration.setDefaultEncoding("UTF-8");
        configuration.setTemplateExceptionHandler(TemplateExceptionHandler.IGNORE_HANDLER);
        return configuration;
    }

    /**
     * 生成 Controller
     *
     * @param tableName
     * @param modelName
     */
    public static void generateController(String tableName, String modelName) {
        try {
            Configuration configuration = getConfiguration();
            String modelNameUpperCamel = StringUtils.isBlank(modelName) ? tableNameConvertUpperCamel(tableName) : modelName;
            String modelNameLowerCamel = StringUtils.isBlank(modelName) ? tableNameConvertLowerCamel(tableName) : tableNameConvertLowerCamel(modelName);
            Map<String, Object> params = assembleParamMap(tableName, modelName);
            File file = new File(PROJECT_PATH + JAVA_PATH + PACKAGE_PATH_CONTROLLER + "Controller.java");
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            configuration.getTemplate("").process(params, new FileWriter(file));
            logger.info("====== {}Controller生成完成 ======", "");
        } catch (Exception e) {
            logger.error("====== 生成Controller失败 ======", e);
            throw new RuntimeException("生成Controller失败", e);
        }
    }

    /**
     * 生成 Service 和 ServiceImpl
     *
     * @param tableName
     * @param modelName
     */
    public static void generateService(String tableName, String modelName) {
        try {
            Configuration configuration = getConfiguration();
            String modelNameUpperCamel = StringUtils.isBlank(modelName) ? tableNameConvertUpperCamel(tableName) : modelName;
            Map<String, Object> params = assembleParamMap(tableName, modelName);
            File file = new File(PROJECT_PATH + JAVA_PATH + PACKAGE_PATH_SERVICE + modelNameUpperCamel + "Service.java");
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            configuration.getTemplate("service.ftl").process(params, new FileWriter(file));
            logger.info("====== {}Service.java 生成成功 ======", modelNameUpperCamel);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            File implFile = new File(PROJECT_PATH + JAVA_PATH + PACKAGE_PATH_SERVICE_IMPL + modelNameUpperCamel + "ServiceImpl.java");
            if (!implFile.getParentFile().exists()) {
                implFile.getParentFile().mkdirs();
            }
            configuration.getTemplate("service-impl.ftl").process(params, new FileWriter(implFile));
            logger.info("====== {}ServiceImpl.java 生成成功 ======", modelNameUpperCamel);
        } catch (Exception e) {
            logger.error("====== 生成Service失败 ======", e);
            throw new RuntimeException("生成Service失败", e);
        }
    }

    private static Map<String, Object> assembleParamMap(String tableName, String modelName) {
        Map<String, Object> params = new HashMap<>();
        params.put("date", DATE);
        params.put("author", AUTHOR);
        String modelNameUpperCamel = StringUtils.isBlank(modelName) ? tableNameConvertUpperCamel(tableName) : modelName;
        params.put("baseRequestMapping", modelNameConvertMappingPath(modelNameUpperCamel));
        params.put("basePackage", BASE_PACKAGE);
        params.put("modelNameUpperCamel", modelNameUpperCamel);
        params.put("modelNameLowerCamel", "");
        return params;
    }

    private static String tableNameConvertLowerCamel(String tableName) {
        return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, tableName.toLowerCase());
    }

    private static String tableNameConvertUpperCamel(String tableName) {
        return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, tableName.toLowerCase());
    }

    private static String tableNameConvertMappingPath(String tableName) {
        tableName = tableName.toLowerCase();//兼容使用大写的表名
        return "/" + (tableName.contains("_") ? tableName.replaceAll("_", "/") : tableName);
    }

    private static String modelNameConvertMappingPath(String modelName) {
        String tableName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, modelName);
        return tableNameConvertMappingPath(tableName);
    }

    private static String packageConvertPath(String packageName) {
        return String.format("/%s/", packageName.contains(".") ? packageName.replaceAll("\\.", "/") : packageName);
    }


    public static void main(String[] args) {
        System.err.println(tableNameConvertUpperCamel("t_user"));
        System.err.println(tableNameConvertLowerCamel("ModelName"));
        System.err.println(nameConvertLowerCamel("ModelName"));
        System.err.println(nameConvertLowerCamel("t_user"));
    }

    private static String nameConvertLowerCamel(String name) {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, name);
    }
}
