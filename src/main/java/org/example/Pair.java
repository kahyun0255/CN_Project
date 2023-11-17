package org.example;

import java.io.Serializable;

public class Pair<U, V> implements Serializable
{
    public final U first;       // 쌍의 첫 번째 필드
    public final V second;      // 쌍의 두 번째 필드

    // 지정된 값으로 새 쌍을 구성합니다.
    public Pair(U first, V second)
    {
        this.first = first;
        this.second = second;
    }

    @Override
    // 지정된 객체가 현재 객체와 "같음" 확인
    public boolean equals(Object o)
    {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Pair<?, ?> pair = (Pair<?, ?>) o;

        // 기본 객체의 `equals()` 메서드 호출
        if (!first.equals(pair.first)) {
            return false;
        }
        return second.equals(pair.second);
    }

    @Override
    // 해시 테이블을 지원하기 위해 객체의 해시 코드를 계산합니다.
    public int hashCode()
    {
        // 기본 객체의 해시 코드 사용
        return 31 * first.hashCode() + second.hashCode();
    }

    @Override
    public String toString() {
        return "(" + first + ", " + second + ")";
    }

    // 타입이 지정된 Pair 불변 인스턴스를 생성하기 위한 팩토리 메소드
    public static <U, V> Pair<U, V> of(U a, V b)
    {
        // 전용 생성자를 호출
        return new Pair<>(a, b);
    }
}