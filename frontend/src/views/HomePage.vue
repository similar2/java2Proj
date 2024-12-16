<template>
  <div class="home">
    <h1 class="title">Final Project: Java Thread Analysis</h1>
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
  justify-content: center;
  padding: 40px;
  background: linear-gradient(145deg, #a8d0e6, #f1f8ff); /* 更浅的渐变背景 */
  min-height: 100vh;
  animation: fadeIn 1s ease-out;
}

.title {
  font-size: 3.5rem;
  color: #333; /* 改为较浅的深色 */
  margin-bottom: 50px;
  font-weight: bold;
  text-align: center;
  letter-spacing: 4px;
  text-transform: uppercase;
  animation: fadeInTitle 1.5s ease-out;
  line-height: 1.3;
  font-family: 'Arial', sans-serif;
}

.module-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 30px;
  width: 100%;
  max-width: 1300px;
  margin-top: 50px;
}

.module-card {
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(145deg, #b3a7d4, #84c0f4); /* 更浅的渐变色 */
  color: white;
  text-decoration: none;
  padding: 30px 45px;
  border-radius: 20px;
  font-size: 1.6rem;
  font-weight: 700;
  text-transform: uppercase;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1); /* 轻微的阴影 */
  transition: transform 0.3s ease, box-shadow 0.3s ease, background 0.3s ease, opacity 0.3s ease;
  cursor: pointer;
  opacity: 0;
  animation: fadeInCard 1s ease-out forwards;
}

.module-card:hover {
  transform: scale(1.05) rotate(2deg); /* 增加轻微的旋转和放大效果 */
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.15); /* 增强阴影效果 */
  background: linear-gradient(145deg, #84c0f4, #b3a7d4); /* 修改卡片悬停的颜色渐变 */
}

.module-card:active {
  transform: scale(1.02) rotate(1deg);
  box-shadow: 0 6px 15px rgba(0, 0, 0, 0.1);
}

@keyframes fadeIn {
  0% {
    opacity: 0;
  }
  100% {
    opacity: 1;
  }
}

@keyframes fadeInTitle {
  0% {
    opacity: 0;
    transform: translateY(30px);
  }
  100% {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes fadeInCard {
  0% {
    opacity: 0;
    transform: translateY(30px);
  }
  100% {
    opacity: 1;
    transform: translateY(0);
  }
}

@media (max-width: 1024px) {
  .title {
    font-size: 3rem;
  }

  .module-card {
    font-size: 1.4rem;
    padding: 22px 35px;
  }
}

@media (max-width: 768px) {
  .title {
    font-size: 2.6rem;
  }

  .module-card {
    font-size: 1.2rem;
    padding: 18px 30px;
  }
}
</style>
