<template>
  <q-toolbar class="custom-toolbar">
    <q-avatar class="q-mr-lg">
      <img src="@/assets/img/micon.png" style="height: 100%" class="avatar-img" />
    </q-avatar>
    <q-avatar class="q-mr-lg">
      <img src="@/assets/img/cameraon.png" style="height: 100%" class="avatar-img" />
    </q-avatar>
    <q-avatar class="q-mr-lg">
      <img src="@/assets/img/shareon.png" style="height: 100%" class="avatar-img" />
    </q-avatar>

    <!-- 가운데 정렬을 위한 공간 배분 -->
    <q-space />
    <!-- 투표 -->
    <div class="vote-container">
      <div class="label">지지율</div>
      <div class="bar-container">
        <div class="bar" :style="{ width: supportRateA + '%' }">{{ supportRateA }}%</div>
        <div class="bar" :style="{ width: supportRateB + '%' }">{{ supportRateB }}%</div>
        <div class="bar" :style="{ width: noneRate + '%' }">{{ nonRate }}%</div>
      </div>
    </div>

    <!-- 이것은 나머지 요소들을 오른쪽으로 밀어냅니다 -->
    <q-space />

    <q-btn class="end-speech" label="발언 종료" />
  </q-toolbar>
</template>

<script setup>

</script>

<style scoped>
.q-avatar {
  margin-left: 30px;
  margin-right: 0px;
}
.avatar-img {
  height: 100%; /* 이미지 높이를 부모 요소 높이에 맞춤 */
}

.end-speech {
  background-color: #e8ebf9;
  color: #34227c;
  width: 200px;
  height: 50px;
  border-radius: 4px;
  font-size: 20px;
  margin-right: 30px;
}
.vote-container {
  text-align:center;
  background-color: #34227c;
  color: white;
  padding: 10px;
  height:100%;
  width: 50%;
}

.label {
  margin-bottom: 10px;
}

.bar-container {
  display: flex;
  background-color: white;
}

.bar {
  background-color: #6247aa;
  color: white;
  text-align: center;
  padding: 5px;
  margin-right: 2px; /* 바 사이의 간격 */
}
</style>
