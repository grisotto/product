## CRUD Products

### Create
```json
curl --location --request POST 'http://localhost:8080/api/v1/products' \
--header 'Content-Type: application/json' \
--data-raw '{
"name": "Vaqueiro alto longo",
"description": "Vaqueiro medio longo para usar todos los días."
}'
   ```

### GET
```json
curl --location --request GET 'http://localhost:8080/api/products'
   ```
### GET with filter
```json
curl --location --request GET 'http://localhost:8080/api/products?name=baixo&description=días'
   ```

### Update 
```json
curl --location --request PUT 'http://localhost:8080/api/v1/products/1' \
--header 'Content-Type: application/json' \
--data-raw '{
"name": "Vaqueiro longo",
"description": "Vaqueiro curto para usar todos los días."
}'
   ```

### Delete
```json
curl --location --request DELETE 'http://localhost:8080/api/v1/products/1'
   ```
