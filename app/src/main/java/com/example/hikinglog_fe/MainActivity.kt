package com.example.hikinglog_fe

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.hikinglog_fe.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {
    lateinit var binding : ActivityMainBinding

    // [viewpager adapter에 대한 class]
    class MyFragmentPagerAdapter(activity: FragmentActivity): FragmentStateAdapter(activity){
        // Fragment를 리스트로 가져옴
        val fragments : List<Fragment>
        init{
            fragments = listOf(HomeFragment(), MyTourismFragment(), MapFragment(), CommunityFragment(), MyPageFragment()) //fragments 초기화
        }

        // override 필수: getItemCount, createFragment
        override fun getItemCount(): Int {
            return fragments.size //Fragment 갖고 있는 리스트의 길이 return
        }
        override fun createFragment(position: Int): Fragment { //몇번째 Fragment인지 position이라는 매개변수를 통해 전달.
            //position: 0=PlantSearchFragment, 1=MyPlantFragment... 와 같이 매개변수 position에 맞는 Fragment return 해주는 함수.
            return fragments[position]
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // [액션바 설정]
        supportActionBar?.apply {
            setDisplayShowHomeEnabled(true)
            setLogo(R.drawable.etc_logo) // 로고 이미지 설정
            setDisplayUseLogoEnabled(true)
            title = "산행록"
        }

        // [뷰 페이저2]
        binding.viewpager.adapter = MyFragmentPagerAdapter(this) // viewpager 이용해 Fregment activity에 포함시키기 위해 adapter 사용.

        // [탭 레이아웃 추가]_탭을 이용해 viewpager 좀 더 편리하게
        TabLayoutMediator(binding.tabs, binding.viewpager){ //TabLayoutMediator 사용! - binding에서 탭 레이아웃과 뷰페이저 찾아서 묶음.
                tab, position -> // tab과 tab의 순서 전달됨.
            when(position) {
                0 -> {
                    tab.text = "홈"
                    tab.icon = ContextCompat.getDrawable(this, R.drawable.icon_tab_home)
                }
                1 -> {
                    tab.text = "마이관광"
                    tab.icon = ContextCompat.getDrawable(this, R.drawable.icon_tab_tourism)
                }
                2 -> {
                    tab.text = "지도"
                    tab.icon = ContextCompat.getDrawable(this, R.drawable.icon_tab_map)
                }
                3 -> {
                    tab.text = "커뮤니티"
                    tab.icon = ContextCompat.getDrawable(this, R.drawable.icon_tab_community)
                }
                4 -> {
                    tab.text = "마이"
                    tab.icon = ContextCompat.getDrawable(this, R.drawable.icon_tab_mypage)
                }
            }
        }.attach() // tab 버튼 부착

        // AddPostActivity -> CommunityFragment 돌아기 위한 관련 코드
        // [Intent에서 fragment 선택 처리]
        val fragmentToSelect = intent.getStringExtra("fragment")
        if (fragmentToSelect != null) {
            when (fragmentToSelect) {
                "community" -> binding.viewpager.currentItem = 3 // 커뮤니티 탭으로 이동
            }
        }

    } //onCreate()


    // [Option Menu(액션바 메뉴)]
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_navigation, menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            // 알림
            R.id.menu_alarm -> {
                val intent = Intent(this, AlarmActivity::class.java)
                startActivity(intent)
                true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}