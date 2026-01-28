
# install if needed
# npm install -g artillery

# run the test

export TARGET_URL=http://localhost:8080

echo "Running load test against $TARGET_URL"

artillery run load.yaml
