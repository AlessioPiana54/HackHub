#!/bin/sh
# Porta di ascolto di nginx: sovrascrivibile con la variabile PORT, default 8080
: "${PORT:=8080}"
export PORT
envsubst '${BACKEND_URL} ${PORT}' < /etc/nginx/templates/default.conf.template > /etc/nginx/conf.d/default.conf
exec nginx -g 'daemon off;'
