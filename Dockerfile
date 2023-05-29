# İşletim sistemi ve java jdk eklenir
# FROM amazoncorretto:17 -- amazon corretto ile java jdk 17 sürümü kullanılacak demektir
FROM azul/zulu-openjdk-alpine:17.0.7
# build aldığımız jar dosyasını docker imajının içine kopyalıyoruz
#COPY ConfigServerGit/build/libs/ConfigServerGit-v.0.0.1.jar app.jar
#COPY auth-service/build/libs/auth-service-v.0.0.1.jar app.jar
#COPY userprofile-service/build/libs/userprofile-service-v.0.0.1.jar app.jar
#COPY mail-service/build/libs/mail-service-v.0.0.1.jar app.jar
#COPY post-service/build/libs/post-service-v.0.0.1.jar app.jar
#COPY elastic-service/build/libs/elastic-service-v.0.0.1.jar app.jar
COPY api-gateway-service/build/libs/api-gateway-service-v.0.0.1.jar app.jar
# docker imajımızın çalışması için java uygulamamızı tetikliyoruz
ENTRYPOINT ["java","-jar","/app.jar"]