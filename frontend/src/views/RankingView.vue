<template>
  <header>
    <HeaderComponent />
  </header>
  <div class="ranking-view">
    <div class="categories">
      <div class="title">👑 랭킹</div>
      <div class="category-container">
        <div
          v-for="category in categories"
          :key="category.english"
          class="category-box"
          @click="fetchRankings(category.english)"
        >
          {{ category.name }}
        </div>
      </div>
    </div>
    <div class="rankingform">
      <div class="option">
        <div class="top20">Top 20</div>
        <div class="search">
          <input v-model="text" placeholder="   닉네임을 입력하세요">
          <q-btn label="검색" class="sbtn" @click="searchUserRank(text)" />
        </div>
      </div>
      <div class="ranking-card">
        <div class="ranking-list">
          <div class="ranking-header">
            <span>순위</span>
            <span>닉네임</span>
            <span>티어</span>
            <span>포인트</span>
            <span>승률</span>
          </div>
          <div v-for="(member, index) in rankStore.rankings" :key="member.memberId" class="ranking-item">
            <span>{{ index + 1 }}</span>
            <span>{{ member.nickname }}</span>
            <span>{{ member.rankName }}</span>
            <span>{{ member.pivotPoint }}</span>
            <span>{{ member.winRate }}%</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRankStore } from '@/stores/useRankStore';
import HeaderComponent from '@/components/common/HeaderComponent.vue';
import { categories } from '@/components/category/Category.js';

const rankStore = useRankStore();
const text = ref('');

const fetchRankings = (category) => {
  rankStore.fetchRankings(category);
};

const searchUserRank = (nickname) => {
  rankStore.searchUserRank(nickname);
};
</script>

<style scoped>
.categories {
padding: 20px 50px 50px 50px;
}
.title {
font-size: 1.5rem;
font-weight: bold;
text-align: center;
line-height: 40px;
color: #34227c;
width: 150px;
height: 40px;
}
.option{
    display: flex;
    justify-content: space-between;
    height: 80px;
    margin: 0;
}
.top20{
    font-size: 1.5rem;
    font-weight: 800;
    padding: 10px 20px 45px 0;
    margin: 10px 0 50px 0;
    border-radius: 4px;
    background-color: #34227c;
    color: #e8ebf9;
    text-align: center;
    cursor: pointer;
    transition: background-color 0.3s ease;
    width: 180px;;
    box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.1);
}
.search{
    display:flex;
    margin: 25px 0 5px 0;
    height: 40px;
}

input{
  border: none;
  background-color: white;
  box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.1);
}
input.placeholder{
  font-color: gray;
}
.sbtn{
  background-color:#34227cf9;
  color:#e8ebf9;
  font-weight: bold;
}
.category-container {
display: flex;
flex-wrap: wrap;
justify-content: space-around;
}

.category-box {
/* flex: 1 0 16%; */
font-size: 1.3rem;
font-weight: bold;
padding: 10px 20px;
margin: 5px;
border-radius: 4px;
background-color: #e8ebf9;
color: #34227c;
text-align: center;
cursor: pointer;
transition: background-color 0.3s ease;
width: calc((100% / 6) - 10px);
box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.1);
}

.rankingform{
    max-width: calc(100% - 115px); 
    margin: 20px auto;
}

.ranking-card {
background-color: #e8ebf9;
box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
border-radius: 8px;
overflow: hidden;
}
.ranking-list {
padding: 20px; 
}
.ranking-list .ranking-header {
background-color: #e8ebf9;
color: #34227c;
padding: 15px 0;
font-weight: bold;
border-radius: 6px 6px 0 0;
margin-bottom: 10px;
font-size: 1.3rem;
}

.ranking-list .ranking-header span,
.ranking-list .ranking-item span {
display: inline-block;
width: 20%;
text-align: center;
color: #34227c;

}

.ranking-list .ranking-item {
background-color: white;
color: #34227c;
border-bottom: 1px solid #ddd;
padding: 10px 0;
border-radius: 6px;
margin-bottom: 10px; 
box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.1);
font-size: 1.3rem;
}
.ranking-header{
  box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.1);
}
.ranking-list .ranking-item:last-child {
border-bottom: none;
margin-bottom: 0; 
}
.category-box:hover,
.category-box.active {
background-color: #34227c;
color: #e8e8e8;
}
</style>