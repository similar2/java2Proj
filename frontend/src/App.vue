<template>
  <div id="app">
    <header class="app-header">
      <h1>Java Thread Analysis</h1>
    </header>
    <div class="app-body">
      <aside class="sidebar">
        <ul>
          <li><router-link to="/">Home</router-link></li>
          <li><router-link to="/topic-analysis">Java Topic Analysis</router-link></li>
          <li><router-link to="/engagement-analysis">User Engagement Analysis</router-link></li>
          <li><router-link to="/mistake-analysis">Common Mistakes Analysis</router-link></li>
          <li><router-link to="/answer-quality">Answer Quality Analysis</router-link></li>
          <li><router-link to="/restful-service">RESTful Service</router-link></li>
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

<style>
#app {
  display: flex;
  flex-direction: column;
  height: 100vh;
}

.app-header {
  background-color: #007BFF;
  color: white;
  text-align: center;
  padding: 10px 0;
}

.app-body {
  display: flex;
  flex: 1;
}

.sidebar {
  width: 200px;
  background-color: #f8f9fa;
  padding: 20px;
  border-right: 1px solid #ddd;
}

.sidebar ul {
  list-style: none;
  padding: 0;
}

.sidebar li {
  margin-bottom: 10px;
}

.sidebar li a {
  text-decoration: none;
  color: #007BFF;
  font-weight: bold;
}

.sidebar li a.router-link-exact-active {
  color: #0056b3;
}

.content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}
</style>
