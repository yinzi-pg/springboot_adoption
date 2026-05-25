<template>
  <div class="baidu-map-container">
    <!-- 地图容器 -->
    <div id="baidu-map" class="map-container"></div>
    
    <!-- 操作面板 -->
    <div class="operation-panel">
      <h3 class="panel-title">地点管理</h3>
      
      <!-- 搜索框 -->
      <div class="search-container">
        <input 
          type="text" 
          v-model="searchKeyword" 
          @keyup.enter="handleSearch"
          placeholder="搜索地点..."
          class="search-input"
        >
        <button @click="handleSearch" class="search-btn">搜索</button>
        
        <!-- 搜索结果 -->
        <div v-if="searchResults.length > 0" class="search-results">
          <div class="results-title">搜索结果 ({{ searchResults.length }})</div>
          <ul>
            <li 
              v-for="(result, index) in searchResults" 
              :key="index" 
              class="search-result-item"
              @click="selectSearchResult(result)"
            >
              <div class="result-name">{{ result.name }}</div>
              <div class="result-address">{{ result.address }}</div>
            </li>
          </ul>
        </div>
      </div>
      
      <!-- 地点表单 -->
      <form @submit.prevent="handleSubmit" class="place-form">
        <input type="hidden" v-model="currentPlace.id">
        
        <div class="form-group">
          <label>地点名称:</label>
          <input type="text" v-model="currentPlace.name" required placeholder="请输入地点名称">
        </div>
        
        <div class="form-group">
          <label>地址:</label>
          <input type="text" v-model="currentPlace.address" placeholder="请输入详细地址">
        </div>
        
        <div class="form-group">
          <label>经度:</label>
          <input type="number" step="0.000001" v-model="currentPlace.longitude" required placeholder="点击地图获取或手动输入">
        </div>
        
        <div class="form-group">
          <label>纬度:</label>
          <input type="number" step="0.000001" v-model="currentPlace.latitude" required placeholder="点击地图获取或手动输入">
        </div>
        
        <div class="form-actions">
          <button type="submit" class="btn-primary">{{ currentPlace.id ? '更新' : '添加' }}</button>
          <button type="button" @click="resetForm" v-if="currentPlace.id" class="btn-secondary">取消</button>
        </div>
      </form>
      
      <!-- 地点列表 -->
      <div class="places-list">
        <h4 class="list-title">已保存地点</h4>
        <div class="list-empty" v-if="places.length === 0">
          暂无保存的地点
        </div>
        <ul v-else>
          <li v-for="place in places" :key="place.id" class="place-item">
            <div class="place-info">
              <div class="place-name">{{ place.name }}</div>
              <div class="place-address">{{ place.address }}</div>
            </div>
            <div class="place-actions">
              <button @click="editPlace(place)" class="btn-edit">编辑</button>
              <button @click="deletePlace(place.id)" class="btn-delete">删除</button>
            </div>
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive, watch } from 'vue';
import instance from '@/utils/request'; // 导入axios实例

// 响应式数据
const places = ref([]);
const currentPlace = reactive({
  id: null,
  name: '',
  address: '',
  longitude: 0,
  latitude: 0
});
const searchKeyword = ref('');
const searchResults = ref([]);
const loading = ref(false);
let map = null; // 百度地图实例
let markers = []; // 地图标记点数组
let localSearch = null; // 百度地图本地搜索服务实例

// 初始化地图
const initMap = () => {
  // 确保百度地图API已加载
  if (typeof BMap === 'undefined') {
    console.error('百度地图API未加载，请在index.html中引入');
    return;
  }
  
  // 创建地图实例，中心点可以设置为默认值
  map = new BMap.Map("baidu-map");
  // 可以根据IP自动获取当前城市作为初始中心点
  const myCity = new BMap.LocalCity();
  myCity.get(result => {
    const point = result.center;
    map.centerAndZoom(point, 13); // 初始化地图，设置中心点坐标和地图级别
    
    // 初始化本地搜索服务
    localSearch = new BMap.LocalSearch(map, {
      renderOptions: { map: map }, // 搜索结果将自动在地图上标记
      pageCapacity: 10 // 每页显示10条结果
    });
  });
  
  map.enableScrollWheelZoom(true); // 启用滚轮缩放
  
  // 地图点击事件，获取经纬度
  map.addEventListener("click", (e) => {
    currentPlace.longitude = e.point.lng;
    currentPlace.latitude = e.point.lat;
  });
};

// 加载所有地点
const loadAllPlaces = async () => {
  loading.value = true;
  try {
    const response = await instance.get('/api/places');
    if (response.success) {
      places.value = response.data;
      addMarkersToMap(); // 在地图上添加标记
    } else {
      console.error('加载地点失败:', response.message);
      alert('加载地点失败: ' + response.message);
    }
  } catch (error) {
    console.error('加载地点时发生错误:', error);
    alert('加载地点时发生错误，请重试');
  } finally {
    loading.value = false;
  }
};

// 在地图上添加所有地点标记
const addMarkersToMap = () => {
  if (!map) return;
  
  // 清除现有标记
  markers.forEach(marker => map.removeOverlay(marker));
  markers = [];
  
  // 添加新标记
  places.value.forEach(place => {
    if (place.longitude && place.latitude) {
      const point = new BMap.Point(place.longitude, place.latitude);
      // 使用不同的图标区分自定义地点和搜索结果
      const icon = new BMap.Icon("http://api.map.baidu.com/img/markers.png", new BMap.Size(23, 25), {
        offset: new BMap.Size(10, 25), // 指定定位位置
        imageOffset: new BMap.Size(0, 0 - 10 * 25) // 设置图片偏移
      });
      const marker = new BMap.Marker(point, {icon: icon});
      
      // 给标记添加点击事件，显示信息窗口
      marker.addEventListener("click", () => {
        const infoWindow = new BMap.InfoWindow(`
          <div style="padding:10px;">
            <h3 style="margin:0 0 10px 0;">${place.name}</h3>
            <p style="margin:0 0 10px 0;">${place.address || '无地址信息'}</p>
            <div style="display:flex;gap:5px;">
              <button onclick="window.editPlace(${place.id})" style="padding:3px 8px;">编辑</button>
              <button onclick="window.deletePlace(${place.id})" style="padding:3px 8px;background:#f44;">删除</button>
            </div>
          </div>
        `);
        map.openInfoWindow(infoWindow, point);
      });
      
      map.addOverlay(marker);
      markers.push(marker);
    }
  });
};

// 处理地点搜索
const handleSearch = () => {
  if (!searchKeyword.value.trim() || !localSearch) return;
  
  // 执行搜索
  localSearch.search(searchKeyword.value);
  
  // 监听搜索完成事件
  localSearch.setSearchCompleteCallback(() => {
    const results = localSearch.getResults();
    if (results && results.getCurrentNumPois()) {
      // 处理搜索结果
      searchResults.value = Array.from(results.getCurrentNumPois()).map(poi => ({
        name: poi.title,
        address: poi.address,
        longitude: poi.point.lng,
        latitude: poi.point.lat,
        city: poi.city
      }));
    } else {
      searchResults.value = [];
      alert('未找到匹配的地点');
    }
  });
};

// 选择搜索结果
const selectSearchResult = (result) => {
  // 将搜索结果填充到表单
  currentPlace.name = result.name;
  currentPlace.address = result.address;
  currentPlace.longitude = result.longitude;
  currentPlace.latitude = result.latitude;
  
  // 清空搜索结果
  searchResults.value = [];
  
  // 地图中心移动到该地点
  const point = new BMap.Point(result.longitude, result.latitude);
  map.centerAndZoom(point, 15);
  
  // 滚动到表单位置
  document.querySelector('.place-form').scrollIntoView({ behavior: 'smooth' });
};

// 表单提交处理（添加或更新）
const handleSubmit = async () => {
  loading.value = true;
  try {
    let response;
    if (currentPlace.id) {
      // 更新地点
      response = await instance.put(`/api/places/${currentPlace.id}`, currentPlace);
    } else {
      // 添加新地点
      response = await instance.post('/api/places', currentPlace);
    }
    
    if (response.success) {
      alert(currentPlace.id ? '更新成功' : '添加成功');
      loadAllPlaces(); // 重新加载地点列表
      resetForm(); // 重置表单
    } else {
      alert('操作失败: ' + response.message);
    }
  } catch (error) {
    console.error('提交时发生错误:', error);
    alert('操作失败，请重试');
  } finally {
    loading.value = false;
  }
};

// 编辑地点
const editPlace = (place) => {
  // 复制地点信息到当前编辑对象
  currentPlace.id = place.id;
  currentPlace.name = place.name;
  currentPlace.address = place.address;
  currentPlace.longitude = place.longitude;
  currentPlace.latitude = place.latitude;
  
  // 地图中心移动到该地点
  const point = new BMap.Point(place.longitude, place.latitude);
  map.centerAndZoom(point, 15);
  
  // 滚动到表单位置
  document.querySelector('.place-form').scrollIntoView({ behavior: 'smooth' });
};

// 删除地点
const deletePlace = async (id) => {
  if (!confirm('确定要删除这个地点吗？')) {
    return;
  }
  
  loading.value = true;
  try {
    const response = await instance.delete(`/api/places/${id}`);
    if (response.success) {
      alert('删除成功');
      loadAllPlaces(); // 重新加载地点列表
      if (currentPlace.id === id) {
        resetForm(); // 如果正在编辑的是被删除的地点，重置表单
      }
    } else {
      alert('删除失败: ' + response.message);
    }
  } catch (error) {
    console.error('删除时发生错误:', error);
    alert('删除失败，请重试');
  } finally {
    loading.value = false;
  }
};

// 重置表单
const resetForm = () => {
  currentPlace.id = null;
  currentPlace.name = '';
  currentPlace.address = '';
  currentPlace.longitude = 0;
  currentPlace.latitude = 0;
  searchKeyword.value = '';
  searchResults.value = [];
};

// 监听地点数据变化，更新地图标记
watch(places, () => {
  addMarkersToMap();
});

// 挂载时初始化
onMounted(() => {
  // 确保DOM已加载完成再初始化地图
  initMap();
  
  // 加载所有地点
  loadAllPlaces();
  
  // 为信息窗口中的按钮添加全局方法
  window.editPlace = (id) => {
    const place = places.value.find(p => p.id === id);
    if (place) {
      editPlace(place);
    }
  };
  
  window.deletePlace = deletePlace;
});
</script>

<style scoped>
.baidu-map-container {
  display: flex;
  height: 100vh;
  width: 100%;
  overflow: hidden;
}

.map-container {
  flex: 1;
  height: 100%;
  z-index: 1;
}

.operation-panel {
  width: 380px;
  padding: 20px;
  background-color: #fff;
  box-shadow: -2px 0 10px rgba(0, 0, 0, 0.1);
  overflow-y: auto;
  z-index: 2;
  max-height: 100vh;
}

.panel-title {
  margin-top: 0;
  color: #333;
  border-bottom: 1px solid #eee;
  padding-bottom: 15px;
}

/* 搜索样式 */
.search-container {
  margin-bottom: 20px;
}

.search-input {
  width: calc(100% - 80px);
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px 0 0 4px;
  font-size: 14px;
  display: inline-block;
}

.search-btn {
  width: 80px;
  padding: 10px;
  background-color: #42b983;
  color: white;
  border: none;
  border-radius: 0 4px 4px 0;
  cursor: pointer;
  display: inline-block;
  vertical-align: top;
}

.search-results {
  margin-top: 10px;
  border: 1px solid #eee;
  border-radius: 4px;
  max-height: 200px;
  overflow-y: auto;
}

.results-title {
  padding: 8px 10px;
  background-color: #f5f5f5;
  font-size: 13px;
  color: #666;
  border-bottom: 1px solid #eee;
}

.search-result-item {
  padding: 10px;
  border-bottom: 1px solid #eee;
  cursor: pointer;
  transition: background-color 0.2s;
}

.search-result-item:hover {
  background-color: #f9f9f9;
}

.result-name {
  font-weight: 500;
  margin-bottom: 3px;
}

.result-address {
  font-size: 12px;
  color: #666;
}

.place-form {
  margin-bottom: 30px;
  padding: 15px;
  background-color: #f9f9f9;
  border-radius: 8px;
}

.form-group {
  margin-bottom: 15px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  font-weight: 500;
  color: #555;
}

.form-group input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.form-group input:focus {
  outline: none;
  border-color: #42b983;
  box-shadow: 0 0 0 2px rgba(66, 185, 131, 0.2);
}

.form-actions {
  margin-top: 20px;
  display: flex;
  gap: 10px;
}

.btn-primary {
  background-color: #42b983;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 4px;
  cursor: pointer;
  flex: 1;
  font-size: 14px;
  transition: background-color 0.3s;
}

.btn-primary:hover {
  background-color: #359e6d;
}

.btn-secondary {
  background-color: #f0f0f0;
  color: #333;
  border: none;
  padding: 10px 20px;
  border-radius: 4px;
  cursor: pointer;
  flex: 1;
  font-size: 14px;
  transition: background-color 0.3s;
}

.btn-secondary:hover {
  background-color: #e0e0e0;
}

.places-list {
  margin-top: 20px;
}

.list-title {
  color: #333;
  margin-bottom: 15px;
  font-size: 16px;
}

.list-empty {
  color: #999;
  text-align: center;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 4px;
}

.place-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  border-bottom: 1px solid #eee;
  transition: background-color 0.2s;
}

.place-item:hover {
  background-color: #f5f5f5;
}

.place-info {
  flex: 1;
}

.place-name {
  font-weight: 500;
  margin-bottom: 4px;
  color: #333;
}

.place-address {
  font-size: 12px;
  color: #666;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 200px;
}

.place-actions {
  display: flex;
  gap: 5px;
}

.btn-edit {
  background-color: #42b983;
  color: white;
  border: none;
  padding: 5px 10px;
  border-radius: 3px;
  cursor: pointer;
  font-size: 12px;
}

.btn-delete {
  background-color: #ff4444;
  color: white;
  border: none;
  padding: 5px 10px;
  border-radius: 3px;
  cursor: pointer;
  font-size: 12px;
}

/* 加载状态样式 */
.loading {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 100;
}
</style>