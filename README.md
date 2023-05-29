docker run --name localdockermongo -p:27017:27017 -e MONGO_INITDB_ROOT_USERNAME=admin -e MONGO_INITDB_ROOT_PASSWORD=root -d mongo

mongo yaratma süreci ilk bu yazılacak burdan yaparken bu docer a mongo yüklicek !!


data base e yetki verme 

db.createUser({user: "JavaUser", pwd: "root", roles:["readWrite", "dbAdmin"]})

iqeoatvrwnfuwfwp


# 17- Auth-service' de yapılan forgotPassword işleminde kullanıcının şifresi,
# üretilen yeni bir şifre ile değiştirilmektedir.
## Bu değişikliğin userprofile-service' e de aktarılması için OpenFeign ,
# yöntemi ile gerekli işlemleri yapınız.

# SECURITY
## Temelde iki çeşittir.
### 1- Token Tabanlı Kimlik Doğrulama
### 2- Session(Oturum) Tabanlı Kimlik Doğrulama

#### 1.1. Kullanıcının giriş yapsaıyla beraber kullanıcının id ve role bilgilerine göre özel bir token üretilir.
#### Bu tokendan role bilgisi alınarak doğrulama işlemi yapılır. Kullanıcnın rolü sınırlandırılabilir ve buna göre kullanıcı erişimi kısıtlanabilir.

#### 1.2. Kullanıcı sisteme girer ve arka tarafta kullanıcıya ait bir oturum bilgisi tutulur.
#### Kullanıcı bu bilgi sayesinde belirli bir süre sisteme username ve password yazmadan giriş yapabilir.
#### Temel olarak WebSecurityConfigurerAdapter, HttpSessionListener, AuthenticationSuccessHandler sınıflarından oluşan bir yapıdır.
#### Session tabanlı kimlik doğrulama da token üretilmez. Bunun yerine sunucu taraflı bir session üretilir.
#### Bu oturum bilgisinin içerisinde kullanıcının id, username, email, password, phone gibi uniqe bilgileri vardır.