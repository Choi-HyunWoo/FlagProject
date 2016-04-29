package com.corcow.hw.flagproject.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.corcow.hw.flagproject.Cheeses;
import com.corcow.hw.flagproject.R;

import org.askerov.dynamicgrid.DynamicGridView;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    DynamicGridView fileGridView;
    FileGridAdpater mAdapter;
    int lastPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fileGridView = (DynamicGridView)findViewById(R.id.fileGridView);
        mAdapter = new FileGridAdpater(MainActivity.this, 3);
        fileGridView.setAdapter(mAdapter);

        fileGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                fileGridView.startEditMode(position);
                return true;
            }
        });
        fileGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 파일 실행 or 하위 경로로 이동
            }
        });
        fileGridView.setOnDragListener(new DynamicGridView.OnDragListener() {
            @Override
            public void onDragStarted(int position) {
                // 드래그 시작시 현재 위치 저장
            }
            @Override
            public void onDragPositionsChanged(int oldPosition, int newPosition) {
                // newPosition의 아이템이 파일이고, newPosition에서 3초동안 머무른다면 (값이 3초동안 같다면) 폴더생성!
                Toast.makeText(MainActivity.this, "oldPosition"+oldPosition+", newPosition"+newPosition, Toast.LENGTH_SHORT).show();
                lastPosition = newPosition;
            }
        });
        fileGridView.setOnDropListener(new DynamicGridView.OnDropListener() {
            @Override
            public void onActionDrop() {
                fileGridView.stopEditMode();
                Toast.makeText(MainActivity.this, ""+lastPosition, Toast.LENGTH_SHORT).show();
            }
        });


        for (int i=0; i<20; i++) {
            FileItem item = new FileItem();
            item.fileIconImgResource = R.mipmap.ic_launcher;
            item.fileName = "파일~"+i;
            mAdapter.add(item);
        }
    }
}