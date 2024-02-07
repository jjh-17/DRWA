<template>
  <q-toolbar class="custom-toolbar">
    <q-avatar class="q-mr-lg">
      <img src="@/assets/img/questionmark.png" style="height: 100%" class="avatar-img" />
    </q-avatar>
    <q-avatar class="q-mr-lg">
      <img src="@/assets/img/exclamationmark.png" style="height: 100%" class="avatar-img" />
    </q-avatar>

    <!-- 가운데 정렬을 위한 공간 배분 -->
    <q-space />

    <!-- 현재 진행 상태 및 남은시간 -->
    <div class="q-pa-md q-ma-md text-center">
      {{ headerBarTitle }}
    </div>

    <!-- 이것은 나머지 요소들을 오른쪽으로 밀어냅니다 -->
    <q-space />

    <q-avatar class="q-mr-lg" fit>
      <img src="@/assets/img/settingmark.png" style="height: 100%" class="avatar-img" />
    </q-avatar>
    <q-avatar class="exit" >
      <img src="@/assets/img/exit.png" style="height: 100%" class="avatar-img" />
    </q-avatar>
  </q-toolbar>
</template>

<script setup>
const props = defineProps({
  headerBarTitle: String
})
const headerBarTitle = props.headerBarTitle
</script>

<style>
.custom-toolbar {
  background-color: #34227c;
  color: white;
  height: 70px;
}
.exit {
    margin-right:30px;
}
.q-avatar {
  margin-left:30px;
  margin-right: 0px;
}
.avatar-img {
  height: 100%; /* 이미지 높이를 부모 요소 높이에 맞춤 */
}
</style>
