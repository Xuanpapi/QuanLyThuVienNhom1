import os
import time
import pyodbc

server = os.environ.get('DB_SERVER', 'db')
database = os.environ.get('DB_NAME', 'QuanLyThuVien')
username = os.environ.get('DB_USER', 'sa')
password = os.environ.get('DB_PASSWORD', 'YourStrong@Passw0rd')

connection_string = f"DRIVER={{ODBC Driver 18 for SQL Server}};SERVER={server};UID={username};PWD={password};TrustServerCertificate=yes;"

print("Waiting for SQL Server to be ready...")
conn = None
for i in range(30):
    try:
        conn = pyodbc.connect(connection_string, autocommit=True)
        print("Connected to SQL Server successfully!")
        break
    except Exception as e:
        print(f"Attempt {i+1} failed: {e}")
        time.sleep(5)

if not conn:
    raise Exception("Could not connect to SQL Server.")

cursor = conn.cursor()

# Create database if not exists
cursor.execute(f"IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = '{database}') CREATE DATABASE [{database}];")
print(f"Database {database} created or already exists.")

# Reconnect with database context
connection_string_db = f"DRIVER={{ODBC Driver 18 for SQL Server}};SERVER={server};DATABASE={database};UID={username};PWD={password};TrustServerCertificate=yes;"
conn_db = pyodbc.connect(connection_string_db, autocommit=True)
cursor_db = conn_db.cursor()

def execute_sql_file(filepath):
    print(f"Executing {filepath}...")
    with open(filepath, 'r', encoding='utf-8-sig') as f:
        content = f.read()
    
    # Split by 'GO' (case-insensitive, on its own line)
    batches = []
    current_batch = []
    for line in content.splitlines():
        if line.strip().upper() == 'GO':
            batches.append('\n'.join(current_batch))
            current_batch = []
        else:
            current_batch.append(line)
    if current_batch:
        batches.append('\n'.join(current_batch))
        
    for batch in batches:
        if batch.strip():
            try:
                cursor_db.execute(batch)
            except Exception as e:
                print(f"Error executing batch: {e}\nBatch snippet: {batch[:100]}...")
                raise e

execute_sql_file('/app/database/create_table2.sql')
execute_sql_file('/app/database/insert.sql')

print("Database initialization completed successfully!")
cursor_db.close()
conn_db.close()
