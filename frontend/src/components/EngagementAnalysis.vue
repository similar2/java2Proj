<template>
  <div class="engagement-analysis">
    <h1>User Engagement Analysis</h1>

    <!-- 输入框和按钮 -->
    <div class="input-container">
      <label for="topN">Enter Top N Topics: </label>
      <input type="number" id="topN" v-model.number="topN" min="1" placeholder="Enter a number" />
      <button @click="calculateEngagement">Submit</button>

      <small class="info-text">(For high-reputation users with reputation >= 10000)</small>
    </div>

    <!-- 展示结果 -->
    <ul v-if="topTopics.length > 0">
      <li v-for="topic in topTopics" :key="topic.id" class="topic-item">
        Topic ID: {{ topic.id }} - Engagement Count: {{ topic.engagementCount }}
        <button class="detail-btn" @click="viewDetails(topic.id)">查看详情</button>
      </li>
    </ul>
    <p v-else class="no-data-msg">No data available for the given N.</p>

    <!-- 弹窗显示问题详情 -->
    <div v-if="showDetail" class="modal-overlay">
      <div class="modal">
        <h3>{{ questionDetail.title || 'No data available' }}</h3>
        <p><strong>Owner:</strong> {{ questionDetail.ownerDisplayName || 'N/A' }}</p>
        <p><strong>Tags:</strong> {{ questionDetail.tags || 'N/A' }}</p>
        <button class="modal-close-btn" @click="closeDetail">Close</button>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      activityData: [],
      topTopics: [],
      reputationThreshold: 50000, // Filter for high-reputation users
      topN: 5, // Default value for N
      showDetail: false, // 控制弹窗显示
      questionDetail: {}, // 存储问题详情
    };
  },
  mounted() {
    this.fetchActivityData();
    this.calculateEngagement();  // 默认展示前五个数据
  },
  methods: {
    async fetchActivityData() {
      try {
        const response = await axios.get('api/activity/all');
        if (response.data.code === 0) {
          this.activityData = response.data.data.records;
          this.calculateEngagement();  // 数据获取完后重新计算默认展示
        }
      } catch (error) {
        console.error('Error fetching activity data:', error);
      }
    },

    calculateEngagement() {
      // Filter for high-reputation users
      const highReputationActivities = this.activityData.filter(
          activity => activity.userReputation >= this.reputationThreshold
      );

      // Count engagement per topic
      const topicEngagement = {};

      highReputationActivities.forEach(activity => {
        const { questionId, activityType } = activity;

        // Count only specific activity types (e.g., 'answer', 'accepted_answer', 'comment')
        if (['answer', 'accepted_answer', 'comment'].includes(activityType)) {
          if (!topicEngagement[questionId]) {
            topicEngagement[questionId] = 0;
          }
          topicEngagement[questionId] += 1; // Increment engagement count
        }
      });

      // Sort topics by engagement count in descending order
      const sortedTopics = Object.entries(topicEngagement)
          .map(([id, engagementCount]) => ({ id, engagementCount }))
          .sort((a, b) => b.engagementCount - a.engagementCount);

      // Dynamically select top N topics based on user input
      this.topTopics = sortedTopics.slice(0, this.topN);
    },

    async viewDetails(activityId) {
      try {
        const response = await axios.get(`api/activity/question?activityId=${activityId}`);
        if (response.data.code === 0 && response.data.data) {
          this.questionDetail = response.data.data; // 获取问题详情
          this.showDetail = true; // 打开弹窗
        } else {
          // 如果没有数据，弹窗显示"没有数据"
          this.questionDetail = {};
          this.showDetail = true;
        }
      } catch (error) {
        console.error('Error fetching question details:', error);
        this.questionDetail = {};
        this.showDetail = true; // 仍然打开弹窗显示没有数据
      }
    },

    closeDetail() {
      this.showDetail = false; // 关闭弹窗
    },
  },
};
</script>

<style scoped>
.engagement-analysis {
  font-family: 'Arial', sans-serif;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 10px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  max-width: 800px;
  margin: 20px auto;
}

h1 {
  font-size: 2em;
  color: #333;
  text-align: center;
  margin-bottom: 20px;
}

.input-container {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
  align-items: center;
}

input[type="number"] {
  padding: 8px;
  width: 120px;
  margin-right: 15px;
  border-radius: 4px;
  border: 1px solid #ccc;
  font-size: 1rem;
}

button {
  padding: 8px 16px;
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
  transition: background-color 0.3s;
}

button:hover {
  background-color: #45a049;
}

.info-text {
  font-size: 0.9rem;
  color: #888;
  margin-left: 10px;
}

ul {
  list-style-type: none;
  padding: 0;
}

.topic-item {
  background-color: #fff;
  padding: 15px;
  margin-bottom: 10px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.topic-item button {
  background-color: #2196F3;
  color: white;
  padding: 8px 12px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.topic-item button:hover {
  background-color: #1976D2;
}

.no-data-msg {
  color: #888;
  text-align: center;
  font-size: 1rem;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 999;
}

.modal {
  background: #fff;
  padding: 20px;
  border-radius: 10px;
  width: 80%;
  max-width: 600px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
  animation: fadeIn 0.3s ease-out;
}

.modal h3 {
  font-size: 1.5rem;
  margin-bottom: 15px;
}

.modal p {
  font-size: 1rem;
  color: #555;
  margin-bottom: 10px;
}

.modal-close-btn {
  padding: 10px 20px;
  background-color: #f44336;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
  transition: background-color 0.3s;
}

.modal-close-btn:hover {
  background-color: #e53935;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}
</style>
