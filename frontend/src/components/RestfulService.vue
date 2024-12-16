<template>
  <div class="container">
    <h1 class="title">RESTful Service</h1>

    <!-- Topic Frequency -->
    <div class="section">
      <h2 class="section-title">Topic Frequency</h2>
      <div class="input-group">
        <input v-model="topic" placeholder="Enter topic" class="input-field" />
        <button @click="getTopicFrequency" class="btn">Get Topic Frequency</button>
      </div>
      <p v-if="topicFrequency" class="result">Topic: {{ topic }} - Frequency: {{ topicFrequency }}</p>
    </div>

    <!-- Exception Frequency -->
    <div class="section">
      <h2 class="section-title">Exception Frequency</h2>
      <div class="input-group">
        <input v-model="exception" placeholder="Enter exception" class="input-field" />
        <button @click="getExceptionFrequency" class="btn">Get Exception Frequency</button>
      </div>
      <p v-if="exceptionFrequency" class="result">Exception: {{ exception }} - Frequency: {{ exceptionFrequency }}</p>
    </div>

    <!-- Top N Topics -->
    <div class="section">
      <h2 class="section-title">Top N Topics</h2>
      <div class="input-group">
        <input v-model.number="topNTopics" type="number" placeholder="Enter N" class="input-field" />
        <button @click="getTopNTopics" class="btn">Get Top N Topics</button>
      </div>
      <ul v-if="topNTopicData.length > 0" class="list">
        <li v-for="(item, index) in topNTopicData" :key="index" class="list-item">
          {{ item.tag }} - Frequency: {{ item.frequency }}
        </li>
      </ul>
    </div>

    <!-- Top N Errors/Exceptions -->
    <div class="section">
      <h2 class="section-title">Top N Errors/Exceptions</h2>
      <div class="input-group">
        <input v-model.number="topNExceptions" type="number" placeholder="Enter N" class="input-field" />
        <select v-model="selectedType" class="select-field">
          <option value="error">Error</option>
          <option value="exception">Exception</option>
        </select>
        <button @click="getTopNExceptions" class="btn">Get Top N {{ selectedType }}</button>
      </div>
      <ul v-if="topNExceptionData.length > 0" class="list">
        <li v-for="(item, index) in topNExceptionData" :key="index" class="list-item">
          {{ item.name }} ({{ item.type }}) - Frequency: {{ item.frequency }}
        </li>
      </ul>
    </div>
  </div>
</template>

<script>
import axios from "axios";

export default {
  name: "RestfulService",
  data() {
    return {
      topic: "",
      exception: "",
      topicFrequency: null,
      exceptionFrequency: null,
      topNTopics: 5,
      topNTopicData: [],
      topNExceptions: 5,
      selectedType: "error",
      topNExceptionData: [],
    };
  },
  methods: {
    async getTopicFrequency() {
      if (!this.topic) {
        alert("Please enter a topic!");
        return;
      }
      // 将输入转换为小写
      const topicToSearch = this.topic.toLowerCase();
      try {
        const response = await axios.get(`/api/topic`, {
          params: { topic: topicToSearch },
        });
        if (response.data.code === 0) {
          this.topicFrequency = response.data.data[topicToSearch] || "No data available";
        } else {
          alert("Failed to fetch topic frequency!");
        }
      } catch (error) {
        console.error("Error fetching topic frequency:", error);
      }
    },

    async getExceptionFrequency() {
      if (!this.exception) {
        alert("Please enter an exception name!");
        return;
      }
      // 将输入转换为小写
      const exceptionToSearch = this.exception.toLowerCase();
      try {
        const response = await axios.get(`/api/exception`, {
          params: { exceptionName: exceptionToSearch },
        });
        if (response.data.code === 0) {
          this.exceptionFrequency =
              response.data.data[exceptionToSearch] || "No data available";
        } else {
          alert("Failed to fetch exception frequency!");
        }
      } catch (error) {
        console.error("Error fetching exception frequency:", error);
      }
    },



  async getTopNTopics() {
      if (!this.topNTopics || this.topNTopics <= 0) {
        alert("Please enter a valid number for N!");
        return;
      }
      try {
        const response = await axios.get(`/api/topic/top`, {
          params: { size: this.topNTopics },
        });
        if (response.data.code === 0) {
          this.topNTopicData = response.data.data;
        } else {
          alert("Failed to fetch top N topics!");
        }
      } catch (error) {
        console.error("Error fetching top N topics:", error);
      }
    },

    async getTopNExceptions() {
      if (!this.topNExceptions || this.topNExceptions <= 0) {
        alert("Please enter a valid number for N!");
        return;
      }
      try {
        const response = await axios.get(`/api/exception/top`, {
          params: { size: this.topNExceptions, type: this.selectedType },
        });
        if (response.data.code === 0) {
          this.topNExceptionData = response.data.data;
        } else {
          alert(`Failed to fetch top N ${this.selectedType}!`);
        }
      } catch (error) {
        console.error(`Error fetching top N ${this.selectedType}:`, error);
      }
    },
  },
};
</script>

<style scoped>
.container {
  width: 90%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  background-color: #f9f9f9;
  border-radius: 8px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
}

.title {
  color: #2196f3;
  text-align: center;
  font-size: 2.5rem;
  margin-bottom: 30px;
  text-transform: uppercase;
}

.section {
  margin-bottom: 30px;
}

.section-title {
  color: #4caf50;
  font-size: 1.8rem;
  margin-bottom: 15px;
}

.input-group {
  display: flex;
  align-items: center;
  gap: 15px;
}

.input-field {
  width: 100%;
  padding: 10px;
  font-size: 1rem;
  border: 2px solid #2196f3;
  border-radius: 5px;
  box-sizing: border-box;
  transition: border-color 0.3s;
}

.input-field:focus {
  border-color: #4caf50;
}

.btn {
  padding: 10px 20px;
  background-color: #4caf50;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.btn:hover {
  background-color: #45a049;
}

.select-field {
  padding: 10px;
  font-size: 1rem;
  border: 2px solid #2196f3;
  border-radius: 5px;
  box-sizing: border-box;
  transition: border-color 0.3s;
}

.result {
  font-size: 1.1rem;
  color: #333;
  margin-top: 10px;
}

.list {
  padding-left: 0;
  list-style-type: none;
}

.list-item {
  background-color: #f1f1f1;
  padding: 10px;
  margin-bottom: 5px;
  border-radius: 5px;
  box-shadow: 0 0 5px rgba(0, 0, 0, 0.1);
}

@media (max-width: 768px) {
  .title {
    font-size: 2rem;
  }
  .section-title {
    font-size: 1.6rem;
  }
  .input-field, .btn, .select-field {
    font-size: 0.9rem;
    padding: 8px;
  }
}
</style>
