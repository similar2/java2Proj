<template>
  <div>
    <h1>Answer Quality Analysis</h1>
    <p>This page will analyze the quality of answers.</p>

    <!-- Reputation Data -->
    <h2>Reputation Analysis</h2>
    <div v-if="loadingReputation">Loading reputation data...</div>
    <div v-else>
      <table v-if="reputationData.length > 0">
        <thead>
        <tr>
          <th>User</th>
          <th>Reputation</th>
          <th>Upvotes</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="(item, index) in currentReputationPage" :key="index">
          <td>{{ item.user }}</td>
          <td>{{ item.reputation }}</td>
          <td>{{ item.upvotes }}</td>
        </tr>
        </tbody>
      </table>
      <div v-if="reputationData.length === 0">No reputation data available.</div>
    </div>

    <!-- Pagination Controls -->
    <div class="pagination">
      <button @click="changePage('reputation', 'prev')" :disabled="currentPageReputation <= 1">Previous</button>
      <button @click="changePage('reputation', 'next')" :disabled="currentPageReputation * pageSizeReputation >= reputationData.length">Next</button>
    </div>

    <!-- Time Data -->
    <h2>Time Analysis</h2>
    <div v-if="loadingTime">Loading time data...</div>
    <div v-else>
      <table v-if="timeData.length > 0">
        <thead>
        <tr>
          <th>Answer Time</th>
          <th>Question Time</th>
          <th>Elapsed Time</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="(item, index) in currentTimePage" :key="index">
          <td>{{ item.answerTime }}</td>
          <td>{{ item.questionTime }}</td>
          <td>{{ item.elapsedTime }}</td>
        </tr>
        </tbody>
      </table>
      <div v-if="timeData.length === 0">No time data available.</div>
    </div>

    <!-- Pagination Controls -->
    <div class="pagination">
      <button @click="changePage('time', 'prev')" :disabled="currentPageTime <= 1">Previous</button>
      <button @click="changePage('time', 'next')" :disabled="currentPageTime * pageSizeTime >= timeData.length">Next</button>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'AnswerQuality',
  data() {
    return {
      // Data for reputation and time
      reputationData: [],
      timeData: [],

      // Loading states
      loadingReputation: false,
      loadingTime: false,

      // Pagination configuration
      currentPageReputation: 1,
      currentPageTime: 1,
      pageSizeReputation: 10,  // Number of items per page for reputation data
      pageSizeTime: 10,        // Number of items per page for time data
    };
  },
  mounted() {
    // Initially fetch data for both reputation and time
    this.fetchData('reputation');
    this.fetchData('time');
  },
  methods: {
    // Method to fetch all data without pagination
    async fetchData(type) {
      if (type === 'reputation') {
        this.loadingReputation = true;
        try {
          const response = await axios.get('api/answer/reputation');
          if (response.data.code === 200) {
            this.reputationData = response.data.data;
          }
        } catch (error) {
          console.error("Error fetching reputation data:", error);
        } finally {
          this.loadingReputation = false;
        }
      } else if (type === 'time') {
        this.loadingTime = true;
        try {
          const response = await axios.get('api/answer/time');
          if (response.data.code === 200) {
            this.timeData = response.data.data;
          }
        } catch (error) {
          console.error("Error fetching time data:", error);
        } finally {
          this.loadingTime = false;
        }
      }
    },

    // Method to handle page change for reputation or time data
    changePage(type, direction) {
      if (type === 'reputation') {
        if (direction === 'prev' && this.currentPageReputation > 1) {
          this.currentPageReputation--;
        } else if (direction === 'next' && this.currentPageReputation * this.pageSizeReputation < this.reputationData.length) {
          this.currentPageReputation++;
        }
      } else if (type === 'time') {
        if (direction === 'prev' && this.currentPageTime > 1) {
          this.currentPageTime--;
        } else if (direction === 'next' && this.currentPageTime * this.pageSizeTime < this.timeData.length) {
          this.currentPageTime++;
        }
      }
    },
  },
  computed: {
    // Calculate the current page data for reputation
    currentReputationPage() {
      const start = (this.currentPageReputation - 1) * this.pageSizeReputation;
      const end = start + this.pageSizeReputation;
      return this.reputationData.slice(start, end);
    },
    // Calculate the current page data for time
    currentTimePage() {
      const start = (this.currentPageTime - 1) * this.pageSizeTime;
      const end = start + this.pageSizeTime;
      return this.timeData.slice(start, end);
    },
  },
};
</script>

<style scoped>
/* Add styles for the AnswerQuality page */
h1 {
  color: #333;
}
h2 {
  color: #555;
}
table {
  width: 100%;
  border-collapse: collapse;
}
th, td {
  padding: 8px 12px;
  text-align: left;
  border: 1px solid #ddd;
}
.pagination {
  margin-top: 20px;
  text-align: center;
}
button {
  padding: 5px 15px;
  margin: 0 5px;
  background-color: #007bff;
  color: white;
  border: none;
  cursor: pointer;
}
button:disabled {
  background-color: #ccc;
}
</style>
