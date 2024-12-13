<template>
  <div>
    <h1>User Engagement Analysis</h1>
    <div id="user-engagement-chart" style="width: 600px; height: 400px;"></div>
    <div id="top-topics-chart" style="width: 600px; height: 400px; margin-top: 20px;"></div>

    <!-- 分页导航 -->
    <div class="pagination" style="margin-top: 20px; text-align: center;">
      <button @click="changePage(currentPage - 1)" :disabled="currentPage === 1">Previous</button>
      <span>Page {{ currentPage }} / {{ totalPages }}</span>
      <button @click="changePage(currentPage + 1)" :disabled="currentPage === totalPages">Next</button>
    </div>
  </div>
</template>

<script>
import * as echarts from 'echarts';
import axios from 'axios';

export default {
  name: 'EngagementAnalysis',
  data() {
    return {
      allUserEngagementData: [], // 所有用户参与度数据
      userEngagementData: [],   // 当前分页显示的用户数据
      highReputationUsers: [],  // 高声誉用户数据
      topTopics: [],            // 常见话题数据
      currentPage: 1,           // 当前页码
      pageSize: 10,             // 每页显示的条数
    };
  },
  computed: {
    totalPages() {
      // 总页数
      return Math.ceil(this.allUserEngagementData.length / this.pageSize);
    },
  },
  mounted() {
    this.fetchUserEngagementData();
  },
  methods: {
    // 获取用户参与度数据
    async fetchUserEngagementData() {
      try {
        const reputationResponse = await axios.get('api/answer/reputation');
        const timeResponse = await axios.get('api/answer/time');

        // 确保数据格式正确
        if (reputationResponse.data.code !== 0 || timeResponse.data.code !== 0) {
          throw new Error('API返回错误');
        }

        // 全量数据
        const reputationData = reputationResponse.data.data; // 嵌套字段
        const timeData = timeResponse.data.data; // 假设两者长度一致

        this.allUserEngagementData = reputationData.map((user, index) => ({
          name: `User ${index + 1}`, // 假设没有用户名则生成默认名称
          engagementScore: user.reputationScore * (timeData[index]?.answerTime || 1), // 示例计算
        }));

        // 初始化第一页数据
        this.updatePageData();
      } catch (error) {
        console.error('Error fetching user engagement data:', error);
      }
    },

    // 更新当前页数据
    updatePageData() {
      const start = (this.currentPage - 1) * this.pageSize;
      const end = start + this.pageSize;
      this.userEngagementData = this.allUserEngagementData.slice(start, end);

      this.renderUserEngagementChart(); // 每次更新数据后重新绘制图表
    },

    // 改变页码
    changePage(newPage) {
      if (newPage < 1 || newPage > this.totalPages) return;
      this.currentPage = newPage;
      this.updatePageData();
    },

    // 绘制用户参与度图表
    renderUserEngagementChart() {
      const chartDom = document.getElementById('user-engagement-chart');
      const myChart = echarts.init(chartDom);
      const option = {
        title: {
          text: 'User Engagement',
          left: 'center',
        },
        tooltip: {
          trigger: 'item',
        },
        xAxis: {
          type: 'category',
          data: this.userEngagementData.map((data) => data.name),
        },
        yAxis: {
          type: 'value',
        },
        series: [
          {
            data: this.userEngagementData.map((data) => data.engagementScore),
            type: 'bar',
            barWidth: '60%',
          },
        ],
      };

      myChart.setOption(option);
    },
  },
};
</script>
