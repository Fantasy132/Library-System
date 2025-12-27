# 构建阶段
FROM maven:3.9-eclipse-temurin-21-alpine AS builder

WORKDIR /build

# 复制所有必要文件
COPY pom.xml .
COPY library-common ./library-common
COPY library-borrow ./library-borrow

# 修改父pom.xml，移除不需要的模块
RUN sed -i '/<module>library-gateway<\/module>/d' pom.xml && \
    sed -i '/<module>library-auth<\/module>/d' pom.xml && \
    sed -i '/<module>library-book<\/module>/d' pom.xml

# 构建项目
RUN mvn clean package -DskipTests -pl library-borrow -am

# 运行阶段
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# 创建非 root 用户
RUN addgroup -S spring && adduser -S spring -G spring

# 创建日志目录并设置权限
RUN mkdir -p /app/logs && chown -R spring:spring /app/logs

# 从构建阶段复制 JAR 文件
COPY --from=builder /build/library-borrow/target/*.jar app.jar

# 修改文件所有者
RUN chown spring:spring app.jar

# 切换到非 root 用户
USER spring:spring

# 暴露端口
EXPOSE 8083

# 健康检查
HEALTHCHECK --interval=30s --timeout=10s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8083/actuator/health || exit 1

# JVM 参数优化
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"

# 启动应用
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
