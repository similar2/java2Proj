<template>
  <div class="home">
    <h1>Final Project: Java Thread Analysis</h1>
    <div class="module-list">
      <router-link to="/topic-analysis" class="module-card">Java Topic Analysis</router-link>
      <router-link to="/engagement-analysis" class="module-card">User Engagement Analysis</router-link>
      <router-link to="/mistake-analysis" class="module-card">Common Mistakes Analysis</router-link>
      <router-link to="/answer-quality" class="module-card">Answer Quality Analysis</router-link>
      <router-link to="/restful-service" class="module-card">RESTful Service</router-link>
    </div>
  </div>
</template>

<script>
export default {
  name: 'HomePage',
};
</script>

<style scoped>
.home {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
}

.module-list {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-top: 20px;
}

.module-card {
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #007BFF;
  color: white;
  text-decoration: none;
  padding: 15px;
  border-radius: 8px;
  font-size: 18px;
  transition: transform 0.3s, background-color 0.3s;
}

.module-card:hover {
  background-color: #0056b3;
  transform: scale(1.05);
}
</style>
