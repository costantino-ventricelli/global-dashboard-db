# Deploy Pipeline for global-dashboard-db

$ErrorActionPreference = "Stop"

Write-Host "Starting Deployment Pipeline..."

# 0. Check Kubernetes Connectivity & Context
Write-Host "Checking Kubernetes Context..."
$currentContext = kubectl config current-context
# Remove newlines etc
$currentContext = $currentContext.Trim()

if ($currentContext -ne "docker-desktop") {
    Write-Error "DANGEROUS OPERATION BLOCKED! Current Context: $currentContext. Expected: docker-desktop"
    exit 1
}
Write-Host "Context confirmed: $currentContext"

Write-Host "Checking Cluster Reachability..."
kubectl cluster-info
if ($LASTEXITCODE -ne 0) { 
    Write-Error "Cannot connect to Kubernetes Cluster. Check your kubeconfig or Docker Desktop status."
    exit 1 
}

# 1. Build Java Application
Write-Host "Building Micronaut Application..."
./gradlew clean build
if ($LASTEXITCODE -ne 0) { Write-Error "Gradle Build Failed"; exit 1 }

# 2. Build Docker Image
Write-Host "Building Docker Image..."
# Using --no-cache to ensure we actually rebuild if the user suspects it's missing
docker build --no-cache -t global-dashboard-db:latest .
if ($LASTEXITCODE -ne 0) { Write-Error "Docker Build Failed"; exit 1 }

# Check if image exists now
$imgCheck = docker images -q global-dashboard-db:latest
if (-not $imgCheck) {
    Write-Error "Docker image 'global-dashboard-db:latest' was NOT found after build. Something is wrong with Docker."
    exit 1
}
else {
    Write-Host "Docker Image verified locally."
}

# 3. Deploy Strategy (Namespace -> Helm)
$namespace = "global-dashboard"
Write-Host "Preparing Namespace $namespace..."
kubectl create namespace $namespace --dry-run=client -o yaml | kubectl apply -f -

Write-Host "Deploying to Kubernetes (Namespace: $namespace)..."

# We ignore errors here because if it's not found, kubectl returns exit code 1, which we want to handle gracefully
$deploymentName = "global-dashboard-db"
$exists = $false
try {
    kubectl get deployment $deploymentName --namespace $namespace -o name 2>$null
    if ($LASTEXITCODE -eq 0) { $exists = $true }
}
catch {
    $exists = $false
}

if ($exists) {
    Write-Host "Deployment $deploymentName found. Performing UPGRADE..."
    helm upgrade $deploymentName ./charts/global-dashboard-db --namespace $namespace
}
else {
    Write-Host "Deployment $deploymentName NOT found. Performing INSTALL..."
    helm install $deploymentName ./charts/global-dashboard-db --namespace $namespace
}

if ($LASTEXITCODE -ne 0) { Write-Error "Helm Operation Failed"; exit 1 }

# 4. Restart Deployment to pick up new image
Write-Host "Restarting Pods..."
kubectl rollout restart deployment/$deploymentName --namespace $namespace

Write-Host "Deployment Complete!"
kubectl get pods -n $namespace
