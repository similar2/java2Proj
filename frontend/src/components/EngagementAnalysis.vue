<template>
  <div class="engagement-analysis">
    <h1>User Engagement Analysis</h1>

    <!-- 切换按钮和输入框 -->
    <div class="switch-container">
      <button @click="toggleView">
        {{ isTagRanking ? 'Switch to Engagement Ranking' : 'Switch to Tag Ranking' }}
      </button>
    </div>

    <!-- 输入框和按钮 (对于标签高质量排行) -->
    <div v-if="isTagRanking" class="input-container">
      <label for="minReputation">Enter Min Reputation: </label>
      <input type="number" id="minReputation" v-model.number="minReputation" min="1" placeholder="Min Reputation" />

      <label for="limit">Enter Top N Tags: </label>
      <input type="number" id="limit" v-model.number="limit" min="1" placeholder="Top N Tags" />

      <button @click="fetchTopTags">Submit</button>
    </div>

    <!-- 展示结果 (Engagement Ranking) -->
    <ul v-if="!isTagRanking && topTopics.length > 0">
      <li v-for="topic in topTopics" :key="topic.id" class="topic-item">
        Topic ID: {{ topic.id }} - Engagement Count: {{ topic.engagementCount }}
        <button class="detail-btn" @click="viewDetails(topic.id)">查看详情</button>
      </li>
    </ul>

    <!-- 展示结果 (Tag Ranking) -->
    <div v-if="isTagRanking && tagRanking.length > 0" class="tag-ranking-container">
      <table class="tag-ranking-table">
        <thead>
        <tr>
          <th>ID</th>
          <th>Tag</th>
          <th>Count</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="(tag, index) in tagRanking" :key="index">
          <td>{{ index + 1 }}</td>
          <td>{{ tag.tag }}</td>
          <td>{{ tag.count }}</td>
        </tr>
        </tbody>
      </table>
    </div>


    <p v-else class="no-data-msg">No data available for the given parameters.</p>

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
      topTopics: [], // 用于存储用户参与度排行
      tagRanking: [], // 用于存储标签排行
      reputationThreshold: 50000, // 高声望用户过滤器
      minReputation: 10000, // 默认最小声望
      limit: 10, // 默认返回的标签数
      topN: 5, // 默认值，用于参与度排行
      isTagRanking: false, // 控制是否展示标签排行
      showDetail: false, // 控制弹窗显示
      questionDetail: {}, // 存储问题详情
    };
  },
  mounted() {
    this.fetchActivityData();
  },
  methods: {
    async fetchActivityData() {
      try {
        const response = await axios.get('api/activity/all');
        if (response.data.code === 0) {
          this.activityData = response.data.data.records;
          this.calculateEngagement(); // 数据获取完后重新计算默认展示
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

    async fetchTopTags() {
      try {
        const response = await axios.get('/api/activity/top-tags', {
          params: {
            minReputation: this.minReputation,
            limit: this.limit,
          },
        });

        if (response.data.code === 0) {
          this.tagRanking = response.data.data.map(item => {
            item.tag = item.tag.replace(/["[\]\\]/g, '');
            return item;
          });

          console.log('Top tags:', this.tagRanking);
        } else {
          this.tagRanking = [];
          console.log('No data available for the given parameters.');
          console.log(response.data);
        }
      } catch (error) {
        console.error('Error fetching top tags:', error);
      }
    },


    toggleView() {
      this.isTagRanking = !this.isTagRanking;

      if (!this.isTagRanking) {
        this.calculateEngagement();
      }
    },

    async viewDetails(activityId) {
      try {
        const response = await axios.get(`api/activity/question?activityId=${activityId}`);
        if (response.data.code === 0 && response.data.data) {
          this.questionDetail = response.data.data;
          this.showDetail = true;
        } else {
          this.questionDetail = {};
          this.showDetail = true;
        }
      } catch (error) {
        console.error('Error fetching question details:', error);
        this.questionDetail = {};
        this.showDetail = true;
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

.tag-ranking-container {
  margin-top: 20px;
  overflow-x: auto;
}

.tag-ranking-table {
  width: 100%;
  border-collapse: collapse;
  text-align: left;
  background-color: #fff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  border-radius: 8px;
  overflow: hidden;
}

.tag-ranking-table th,
.tag-ranking-table td {
  padding: 12px 15px;
  border-bottom: 1px solid #ddd;
}

.tag-ranking-table th {
  background-color: #f4f4f4;
  font-weight: bold;
}

.tag-ranking-table tr:nth-child(even) {
  background-color: #f9f9f9;
}

.tag-ranking-table tr:hover {
  background-color: #f1f1f1;
}

.tag-ranking-table td {
  color: #555;
}

</style>
