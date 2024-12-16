<template>
  <div id="app">
    <header class="app-header">
      <h1>Java Thread Analysis</h1>
    </header>
    <div class="app-body">
      <aside class="sidebar">
        <ul>
          <li><router-link to="/" class="sidebar-link">Home</router-link></li>
          <li><router-link to="/topic-analysis" class="sidebar-link">Java Topic Analysis</router-link></li>
          <li><router-link to="/engagement-analysis" class="sidebar-link">User Engagement Analysis</router-link></li>
          <li><router-link to="/mistake-analysis" class="sidebar-link">Common Mistakes Analysis</router-link></li>
          <li><router-link to="/answer-quality" class="sidebar-link">Answer Quality Analysis</router-link></li>
          <li><router-link to="/restful-service" class="sidebar-link">RESTful Service</router-link></li>
        </ul>
      </aside>
      <main class="content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script>
export default {
  name: 'App',
};
</script>

<style scoped>
/* Container styles */
#app {
  display: flex;
  flex-direction: column;
  height: 100vh;
  font-family: 'Arial', sans-serif;
}

/* Header styles */
.app-header {
  background: linear-gradient(145deg, #007BFF, #0056b3);
  color: white;
  text-align: center;
  padding: 15px 0;
  font-size: 1rem;
  font-weight: bold;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

/* Body layout */
.app-body {
  display: flex;
  flex: 1;
}

/* Sidebar styles */
.sidebar {
  width: 220px;
  background-color: #f1f5f9;
  padding: 20px;
  border-right: 2px solid #ddd;
  box-shadow: 4px 0 8px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  height: 100%;
}

.sidebar ul {
  list-style: none;
  padding: 0;
}

.sidebar li {
  margin-bottom: 12px;
}

.sidebar-link {
  text-decoration: none;
  color: #007BFF;
  font-weight: 600;
  transition: color 0.3s ease, transform 0.3s ease;
  display: block;
}

.sidebar-link:hover {
  color: #0056b3;
  transform: translateX(4px);
}

.sidebar-link.router-link-exact-active {
  color: #0056b3;
}

/* Main content area */
.content {
  flex: 1;
  padding: 20px;
  background-color: #ffffff;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  overflow-y: auto;
  margin-left: 20px;
}

/* Mobile responsiveness */
@media (max-width: 768px) {
  .app-header {
    font-size: 1.5rem;
  }

  .sidebar {
    width: 180px;
    padding: 15px;
  }

  .content {
    margin-left: 10px;
    padding: 15px;
  }
}
</style>
