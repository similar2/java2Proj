// src/router/index.js
import { createRouter, createWebHistory } from 'vue-router';
import Home from '@/views/HomePage.vue';
import TopicAnalysis from '@/components/TopicAnalysis.vue';
import EngagementAnalysis from '@/components/EngagementAnalysis.vue';
import MistakeAnalysis from '@/components/MistakeAnalysis.vue';
import AnswerQuality from '@/components/AnswerQuality.vue';
import RestfulService from '@/components/RestfulService.vue';

const routes = [
    { path: '/', name: 'Home', component: Home },
    { path: '/topic-analysis', name: 'TopicAnalysis', component: TopicAnalysis },
    { path: '/engagement-analysis', name: 'EngagementAnalysis', component: EngagementAnalysis },
    { path: '/mistake-analysis', name: 'MistakeAnalysis', component: MistakeAnalysis },
    { path: '/answer-quality', name: 'AnswerQuality', component: AnswerQuality },
    { path: '/restful-service', name: 'RestfulService', component: RestfulService },
];

const router = createRouter({
    history: createWebHistory(),
    routes,
});

export default router;
