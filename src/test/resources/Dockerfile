FROM mysql:5.7.34
RUN mkdir -p /workdir/mysql/
ENV MYSQL_ROOT_PASSWORD root
# 拷贝初始化sql脚本
COPY ./mysqld.cnf /etc/mysql/mysql.conf.d/mysqld.cnf
COPY ./struct.sql /docker-entrypoint-initdb.d/
#CMD ["sh", "/mysql/setup.sh"]