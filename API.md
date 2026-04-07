# Task Manager API Documentation

## Base URL

```
http://localhost:8080/api/v1
```

**Note:** All API endpoints are prefixed with `/api/v1`. This versioning allows for future API iterations without breaking existing clients.

---

## Authentication

Currently, the API does **not** require authentication. All endpoints are publicly accessible.

---

## Endpoints

### 1. List Tasks (with Pagination)

Retrieve a paginated list of all tasks, sorted by creation date (newest first).

**Request:**
```
GET /tasks?page={page}&size={size}
```

**Query Parameters:**

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `page` | integer | `0` | Zero-based page index (e.g., 0 = first page) |
| `size` | integer | `20` | Number of tasks per page (max: 100) |

**Response:** `200 OK`

```json
{
  "data": [
    {
      "id": 1,
      "title": "Complete project documentation",
      "description": "Write API documentation",
      "status": "IN_PROGRESS",
      "dueDate": "2026-04-15T14:30:00",
      "createdAt": "2026-04-07T10:23:00",
      "updatedAt": "2026-04-07T12:45:00"
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 45,
  "totalPages": 3,
  "hasNext": true,
  "hasPrevious": false
}
```

**Response Fields:**

| Field | Type | Description |
|-------|------|-------------|
| `data` | array | Array of task objects |
| `page` | integer | Current page index (zero-based) |
| `size` | integer | Page size requested |
| `totalElements` | long | Total number of tasks in database |
| `totalPages` | integer | Total number of pages available |
| `hasNext` | boolean | Whether there is a next page |
| `hasPrevious` | boolean | Whether there is a previous page |

**Example:**
```bash
curl "http://localhost:8080/api/v1/tasks?page=0&size=10"
```

---

### 2. Get Task by ID

Retrieve a single task by its unique identifier.

**Request:**
```
GET /tasks/{id}
```

**Path Parameters:**

| Parameter | Type | Description |
|-----------|------|-------------|
| `id` | long | Task ID |

**Response:** `200 OK`

```json
{
  "id": 1,
  "title": "Complete project documentation",
  "description": "Write API documentation",
  "status": "DONE",
  "dueDate": "2026-04-15T14:30:00",
  "createdAt": "2026-04-07T10:23:00",
  "updatedAt": "2026-04-07T14:30:00"
}
```

**Error Responses:**

- `404 Not Found` - Task with given ID doesn't exist

**Example:**
```bash
curl "http://localhost:8080/api/v1/tasks/1"
```

---

### 3. Create Task

Create a new task.

**Request:**
```
POST /tasks
Content-Type: application/json
```

**Request Body:**

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `title` | string | **yes** | Task title (max 255 chars) |
| `description` | string | no | Task description (optional) |
| `status` | enum | **yes** | Task status: `TO_DO`, `IN_PROGRESS`, or `DONE` |
| `dueDate` | string | no | Due date/time in ISO 8601 format: `yyyy-MM-dd'T'HH:mm:ss` |

**Response:** `201 Created`

```json
{
  "id": 1,
  "title": "Complete project documentation",
  "description": "Write API documentation",
  "status": "TO_DO",
  "dueDate": "2026-04-15T14:30:00",
  "createdAt": "2026-04-07T10:23:00",
  "updatedAt": "2026-04-07T10:23:00"
}
```

**Error Responses:**

- `400 Bad Request` - Validation failed (missing title or status)
- `400 Bad Request` - Invalid date format

**Example:**
```bash
curl -X POST "http://localhost:8080/api/v1/tasks" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Complete project documentation",
    "description": "Write API documentation",
    "status": "TO_DO",
    "dueDate": "2026-04-15T14:30:00"
  }'
```

---

### 4. Update Task Status

Partially update a task's status. This is a PATCH endpoint that only updates the status field.

**Request:**
```
PATCH /tasks/{id}/status
Content-Type: application/json
```

**Path Parameters:**

| Parameter | Type | Description |
|-----------|------|-------------|
| `id` | long | Task ID |

**Request Body:**

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `status` | enum | **yes** | New status: `TO_DO`, `IN_PROGRESS`, or `DONE` |

**Response:** `200 OK`

```json
{
  "id": 1,
  "title": "Complete project documentation",
  "description": "Write API documentation",
  "status": "DONE",
  "dueDate": "2026-04-15T14:30:00",
  "createdAt": "2026-04-07T10:23:00",
  "updatedAt": "2026-04-07T15:30:00"
}
```

**Error Responses:**

- `404 Not Found` - Task with given ID doesn't exist
- `400 Bad Request` - Invalid status value

**Example:**
```bash
curl -X PATCH "http://localhost:8080/api/v1/tasks/1/status" \
  -H "Content-Type: application/json" \
  -d '{"status": "DONE"}'
```

---

### 5. Delete Task

Permanently delete a task.

**Request:**
```
DELETE /tasks/{id}
```

**Path Parameters:**

| Parameter | Type | Description |
|-----------|------|-------------|
| `id` | long | Task ID |

**Response:** `204 No Content`

No response body.

**Error Responses:**

- `404 Not Found` - Task with given ID doesn't exist

**Example:**
```bash
curl -X DELETE "http://localhost:8080/api/v1/tasks/1"
```

---

## Data Types

### Task Status Enum

```json
"TO_DO"           // Task is pending
"IN_PROGRESS"     // Task is currently being worked on
"DONE"            // Task is complete
```

### Date/Time Format

All date/time fields use **ISO 8601** format with seconds and UTC timezone:

```
yyyy-MM-dd'T'HH:mm:ss
Example: 2026-04-15T14:30:00
```

---

## Error Handling

All errors return a JSON response with details:

```json
{
  "timestamp": "2026-04-07T10:30:00.123",
  "status": 400,
  "error": "Bad Request",
  "message": "Title is required",
  "path": "/api/v1/tasks"
}
```

**Common Error Codes:**

| Status | Description |
|--------|-------------|
| `400 Bad Request` | Validation error or invalid input |
| `404 Not Found` | Resource doesn't exist |
| `500 Internal Server Error` | Unexpected server error |

---

## Pagination

The `List Tasks` endpoint returns a paginated response. Use the following query parameters to navigate:

- `page=0` (first page)
- `page=1` (second page)
- `size=20` (20 items per page)

The response includes:
- `totalElements` - total count of all tasks
- `totalPages` - total number of pages
- `hasNext` / `hasPrevious` - navigation indicators

**Example pagination flow:**

```bash
# Get first page
curl "http://localhost:8080/api/v1/tasks?page=0&size=20"

# Get second page
curl "http://localhost:8080/api/v1/tasks?page=1&size=20"

# Check if more pages exist
if [ $hasNext = true ]; then
  # fetch next page
fi
```

---

## Rate Limiting

Currently, there is **no rate limiting**. In production, consider implementing rate limiting to prevent abuse.

---

## CORS

The API supports Cross-Origin Resource Sharing (CORS) to allow web applications from `http://localhost:3000` to make requests.

---

## Database

- **Production:** PostgreSQL 16
- **Development/Migrations:** Flyway migrations located in `src/main/resources/db/migration/`
- **Tests:** H2 in-memory database

---

## Contact

For issues or questions, please refer to the project repository.
